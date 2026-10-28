package lms.schedule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.notification.NotificationService;
import lms.schedule.ScheduleViews.Conflict;
import lms.schedule.ScheduleViews.ImportIssue;
import lms.schedule.ScheduleViews.PlannedRule;
import lms.schedule.ScheduleViews.ScheduleImportReport;

/**
 * Nhập thời khóa biểu hàng loạt (admin). Mỗi dòng hợp lệ thành một lịch lặp như tạo bằng form, cùng kiểm tra trùng
 * phòng / giảng viên / khóa học với lịch đã có và với các dòng khác trong file. Tất cả hoặc không: chỉ cần một dòng
 * lỗi là không lưu dòng nào, để admin sửa file rồi nhập lại (nhập nửa chừng rất khó dọn). dryRun để xem trước.
 */
@Service
public class ScheduleImportService {

    /** Số dòng tối đa một lần nhập (cỡ thời khóa biểu cả trường một học kỳ). */
    static final int MAX_LINES = 1000;
    private static final int MAX_BODY = 2000;

    private final ScheduleService schedules;
    private final CourseScheduleRepository rules;
    private final ClassMeetingRepository meetings;
    private final RoomRepository rooms;
    private final CourseRepository courses;
    private final NotificationService notifications;
    private final AuditService audit;

    public ScheduleImportService(ScheduleService schedules, CourseScheduleRepository rules,
            ClassMeetingRepository meetings, RoomRepository rooms, CourseRepository courses,
            NotificationService notifications, AuditService audit) {
        this.schedules = schedules;
        this.rules = rules;
        this.meetings = meetings;
        this.rooms = rooms;
        this.courses = courses;
        this.notifications = notifications;
        this.audit = audit;
    }

    /** Dòng đã khớp khóa học, phòng và các ngày học; chờ kiểm tra trùng giữa các dòng rồi lưu. */
    private record Plan(ScheduleImportParser.Row row, Course course, Room room, List<LocalDate> dates) {
    }

    @Transactional
    public ScheduleImportReport importSchedules(String content, boolean dryRun, boolean notify, AuthPrincipal me) {
        if (content == null || content.isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Content is empty");
        }
        ScheduleImportParser.Result parsed;
        try {
            parsed = new ScheduleImportParser(MAX_LINES).parse(content);
        } catch (ScheduleImportParser.TooManyLinesException e) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
        }
        List<ImportIssue> errors = new ArrayList<>(parsed.errors().stream()
                .map(e -> new ImportIssue(e.line(), e.label(), e.reason(), null, null)).toList());

        ScheduleRules periodRules = schedules.rules();
        Map<String, List<Course>> courseCache = new HashMap<>();
        Map<String, Optional<Room>> roomCache = new HashMap<>();
        List<Plan> plans = new ArrayList<>();
        for (ScheduleImportParser.Row row : parsed.rows()) {
            String label = row.courseCode() + (row.group() == null ? "" : " · Nhóm " + row.group());
            List<Course> candidates = courseCache.computeIfAbsent(ScheduleImportParser.fold(row.courseCode()),
                    k -> courses.findByCodeWithTeacher(row.courseCode()));
            List<Course> matched = matchGroup(candidates, row.group());
            if (matched.size() != 1) {
                errors.add(new ImportIssue(row.line(), label,
                        matched.isEmpty() ? "COURSE_NOT_FOUND" : "COURSE_AMBIGUOUS", null, null));
                continue;
            }
            Course course = matched.get(0);
            Optional<Room> room = roomCache.computeIfAbsent(ScheduleImportParser.fold(row.room()),
                    k -> rooms.findByName(row.room()));
            if (room.isEmpty() || !room.get().isActive()) {
                errors.add(new ImportIssue(row.line(), label, room.isEmpty() ? "ROOM_NOT_FOUND" : "ROOM_INACTIVE",
                        null, null));
                continue;
            }
            if (!periodRules.isValidSlot(row.startPeriod(), row.periods())) {
                errors.add(new ImportIssue(row.line(), label, ScheduleImportParser.INVALID_PERIODS, null, null));
                continue;
            }
            List<LocalDate> dates = ScheduleRules.weeklyDates(row.weekday(), row.startsOn(), row.endsOn());
            if (dates.isEmpty()) {
                errors.add(new ImportIssue(row.line(), label, "NO_MEETINGS", null, null));
                continue;
            }
            List<Conflict> existing = schedules.findConflicts(course, room.get().getId(), dates, row.startPeriod(),
                    row.periods(), null);
            if (!existing.isEmpty()) {
                errors.add(new ImportIssue(row.line(), label, "CONFLICT", null, existing));
                continue;
            }
            Plan clash = plans.stream().filter(p -> clashes(p, course, room.get(), row, dates)).findFirst()
                    .orElse(null);
            if (clash != null) {
                errors.add(new ImportIssue(row.line(), label, "CONFLICT_IN_FILE", clash.row().line(),
                        List.of(inFileConflict(clash, course, room.get(), dates))));
                continue;
            }
            plans.add(new Plan(row, course, room.get(), dates));
        }
        errors.sort(Comparator.comparingInt(ImportIssue::line));

        boolean save = !dryRun && errors.isEmpty() && !plans.isEmpty();
        if (save) {
            for (Plan p : plans) {
                CourseSchedule s = new CourseSchedule(p.course().getId(), me.userId());
                s.setRoomId(p.room().getId());
                s.setWeekday(p.row().weekday());
                s.setStartPeriod(p.row().startPeriod());
                s.setPeriods(p.row().periods());
                s.setKind(p.row().kind());
                s.setStartsOn(p.row().startsOn());
                s.setEndsOn(p.row().endsOn());
                rules.save(s);
                meetings.saveAll(p.dates().stream().map(d -> ScheduleService.newMeeting(s, d)).toList());
            }
            int total = plans.stream().mapToInt(p -> p.dates().size()).sum();
            audit.record(me.userId(), AuditService.SCHEDULES_IMPORTED, plans.size() + " lịch",
                    plans.stream().map(p -> p.course().getId()).distinct().count() + " khóa học, " + total + " buổi");
            if (notify) {
                notifyCourses(plans, me);
            }
        }
        List<PlannedRule> planned = plans.stream().map(p -> new PlannedRule(p.row().line(),
                ScheduleService.courseRef(p.course()), ScheduleService.roomRef(p.room()), p.row().weekday(),
                p.row().startPeriod(), p.row().periods(), p.row().kind(), p.row().startsOn(), p.row().endsOn(),
                p.dates().size())).toList();
        return new ScheduleImportReport(parsed.dataLines(), save, planned,
                plans.stream().mapToInt(p -> p.dates().size()).sum(), errors);
    }

    /**
     * Khóa học theo nhóm: "1" khớp "01", không phân biệt hoa thường. Bỏ trống nhóm: khóa không chia nhóm, hoặc
     * mọi nhóm của học phần (một nhóm thì dùng nhóm đó, nhiều nhóm thì người gọi báo COURSE_AMBIGUOUS).
     */
    static List<Course> matchGroup(List<Course> candidates, String group) {
        if (group == null) {
            List<Course> ungrouped = candidates.stream().filter(c -> c.getGroupName() == null).toList();
            // Không có khóa "không chia nhóm": một khóa thì lấy luôn, nhiều khóa thì báo COURSE_AMBIGUOUS
            return ungrouped.isEmpty() ? candidates : ungrouped;
        }
        String key = groupKey(group);
        return candidates.stream().filter(c -> c.getGroupName() != null && groupKey(c.getGroupName()).equals(key))
                .toList();
    }

    private static String groupKey(String group) {
        String folded = ScheduleImportParser.fold(group);
        return folded.matches("\\d+") ? String.valueOf(Integer.parseInt(folded)) : folded;
    }

    /** Hai lịch trong file trùng: cùng thứ, chung tiết, khoảng ngày giao nhau, cùng phòng / khóa học / giảng viên. */
    private static boolean clashes(Plan p, Course course, Room room, ScheduleImportParser.Row row,
            List<LocalDate> dates) {
        ScheduleImportParser.Row other = p.row();
        if (other.weekday() != row.weekday() || !ScheduleRules.overlaps(dates.get(0), other.startPeriod(),
                other.periods(), dates.get(0), row.startPeriod(), row.periods())) {
            return false;
        }
        if (firstCommonDate(p.dates(), dates) == null) {
            return false;
        }
        return p.room().getId().equals(room.getId()) || p.course().getId().equals(course.getId())
                || teacherId(p.course()) != null && teacherId(p.course()).equals(teacherId(course));
    }

    private static Conflict inFileConflict(Plan p, Course course, Room room, List<LocalDate> dates) {
        String reason = p.room().getId().equals(room.getId()) ? "room"
                : p.course().getId().equals(course.getId()) ? "course" : "teacher";
        return new Conflict(firstCommonDate(p.dates(), dates), p.row().startPeriod(), p.row().periods(), reason,
                ScheduleService.courseRef(p.course()), ScheduleService.roomRef(p.room()));
    }

    /** Hai dãy cùng thứ trong tuần, tăng dần: ngày chung đầu tiên (null nếu khoảng ngày không giao nhau). */
    private static LocalDate firstCommonDate(List<LocalDate> a, List<LocalDate> b) {
        LocalDate start = a.get(0).isAfter(b.get(0)) ? a.get(0) : b.get(0);
        LocalDate end = a.get(a.size() - 1).isBefore(b.get(b.size() - 1)) ? a.get(a.size() - 1) : b.get(b.size() - 1);
        return start.isAfter(end) ? null : start;
    }

    private static Long teacherId(Course c) {
        return c.getTeacher() == null ? null : c.getTeacher().getId();
    }

    /** Mỗi khóa học một thông báo liệt kê các lịch vừa nhập. */
    private void notifyCourses(List<Plan> plans, AuthPrincipal me) {
        Map<Long, List<Plan>> byCourse = plans.stream()
                .collect(Collectors.groupingBy(p -> p.course().getId(), LinkedHashMap::new, Collectors.toList()));
        for (List<Plan> list : byCourse.values()) {
            Course c = list.get(0).course();
            String body = list.stream().map(p -> {
                CourseSchedule s = new CourseSchedule(c.getId(), me.userId());
                s.setWeekday(p.row().weekday());
                s.setStartPeriod(p.row().startPeriod());
                s.setPeriods(p.row().periods());
                s.setStartsOn(p.row().startsOn());
                s.setEndsOn(p.row().endsOn());
                return ScheduleService.describeRule(s, p.room());
            }).collect(Collectors.joining("; "));
            // Cột notifications.body tối đa 2000 ký tự
            if (body.length() > MAX_BODY) {
                body = body.substring(0, MAX_BODY - 1) + "…";
            }
            notifications.scheduleChanged(c, "Lịch học " + c.displayCode(), body, me.userId());
        }
    }
}
