package lms.schedule;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.attendance.AttendanceService;
import lms.attendance.AttendanceSession;
import lms.attendance.AttendanceSessionRepository;
import lms.attendance.AttendanceViews.CreateSessionRequest;
import lms.attendance.AttendanceViews.SessionDetail;
import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.FileStorage;
import lms.leave.LeaveRequest;
import lms.leave.LeaveRequestFileRepository;
import lms.leave.LeaveRequestRepository;
import lms.leave.LeaveService;
import lms.leave.LeaveViews.MyLeave;
import lms.notification.NotificationService;
import lms.schedule.ScheduleViews.AttendanceLink;
import lms.schedule.ScheduleViews.CancelRequest;
import lms.schedule.ScheduleViews.Conflict;
import lms.schedule.ScheduleViews.CourseRef;
import lms.schedule.ScheduleViews.DeadlineView;
import lms.schedule.ScheduleViews.MeetingRequest;
import lms.schedule.ScheduleViews.MeetingView;
import lms.schedule.ScheduleViews.RoomImportReport;
import lms.schedule.ScheduleViews.RoomRef;
import lms.schedule.ScheduleViews.RoomRequest;
import lms.schedule.ScheduleViews.RoomView;
import lms.schedule.ScheduleViews.ScheduleFeed;
import lms.schedule.ScheduleViews.ScheduleRequest;
import lms.schedule.ScheduleViews.ScheduleResult;
import lms.schedule.ScheduleViews.ScheduleView;

/**
 * Lịch học. Admin (schedules.manage) quản lý danh mục phòng, quy tắc lặp hằng tuần của từng khóa học
 * và từng buổi (thêm buổi bù, dời, hủy). Mọi buổi mới đều qua kiểm tra trùng: cùng phòng, cùng giảng viên phụ trách
 * hoặc cùng khóa học mà chung tiết thì SCHEDULE_CONFLICT, không lưu gì. Sinh viên, giảng viên xem lịch của mình;
 * giảng viên phụ trách mở buổi điểm danh ngay từ buổi học.
 */
@Service
public class ScheduleService {

    /** Khoảng ngày tối đa một lần xem lịch (lịch tháng có thể trải 6 tuần). */
    static final int MAX_FEED_DAYS = 62;
    /** Số dòng tối đa một lần nhập phòng. */
    static final int MAX_IMPORT_LINES = 1000;
    /** Số buổi trùng tối đa trả về trong details của SCHEDULE_CONFLICT. */
    private static final int MAX_CONFLICTS = 20;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM");
    private static final String[] WEEKDAYS = { "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật" };

    private final ScheduleRules rules;
    private final RoomRepository rooms;
    private final CourseScheduleRepository schedules;
    private final ClassMeetingRepository meetings;
    private final CourseRepository courses;
    private final CourseService courseService;
    private final AssessmentRepository assessments;
    private final AttendanceService attendance;
    private final AttendanceSessionRepository attendanceSessions;
    private final NotificationService notifications;
    private final AuditService audit;
    private final LeaveService leaves;
    private final LeaveRequestRepository leaveRequests;
    private final LeaveRequestFileRepository leaveFiles;
    private final FileStorage storage;

    public ScheduleService(AppProperties props, RoomRepository rooms, CourseScheduleRepository schedules,
            ClassMeetingRepository meetings, CourseRepository courses, CourseService courseService,
            AssessmentRepository assessments,
            AttendanceService attendance, AttendanceSessionRepository attendanceSessions,
            NotificationService notifications, AuditService audit, LeaveService leaves,
            LeaveRequestRepository leaveRequests, LeaveRequestFileRepository leaveFiles, FileStorage storage) {
        this.rules = new ScheduleRules(props.schedule().firstPeriodHour(), props.schedule().periodsPerDay());
        this.rooms = rooms;
        this.schedules = schedules;
        this.meetings = meetings;
        this.courses = courses;
        this.courseService = courseService;
        this.assessments = assessments;
        this.attendance = attendance;
        this.attendanceSessions = attendanceSessions;
        this.notifications = notifications;
        this.audit = audit;
        this.leaves = leaves;
        this.leaveRequests = leaveRequests;
        this.leaveFiles = leaveFiles;
        this.storage = storage;
    }

    public ScheduleRules rules() {
        return rules;
    }

    // ================================================================ phòng học

    @Transactional(readOnly = true)
    public List<RoomView> listRooms() {
        return rooms.findAllByOrderByNameAsc().stream().map(ScheduleService::roomView).toList();
    }

    @Transactional
    public RoomView createRoom(RoomRequest req, AuthPrincipal me) {
        String name = req.name().trim();
        if (rooms.existsByName(name)) {
            throw new AppException(ErrorCode.ROOM_NAME_TAKEN);
        }
        Room r = new Room(name, CourseService.blankToNull(req.building()), req.capacity(),
                CourseService.blankToNull(req.note()));
        if (req.active() != null) {
            r.setActive(req.active());
        }
        rooms.save(r);
        audit.record(me.userId(), AuditService.ROOM_CREATED, r.getName(), null);
        return roomView(r);
    }

    @Transactional
    public RoomView updateRoom(String roomId, RoomRequest req, AuthPrincipal me) {
        Room r = requireRoom(roomId);
        String name = req.name().trim();
        if (rooms.existsByNameAndIdNot(name, r.getId())) {
            throw new AppException(ErrorCode.ROOM_NAME_TAKEN);
        }
        String old = r.getName();
        r.setName(name);
        r.setBuilding(CourseService.blankToNull(req.building()));
        r.setCapacity(req.capacity());
        r.setNote(CourseService.blankToNull(req.note()));
        if (req.active() != null) {
            r.setActive(req.active());
        }
        audit.record(me.userId(), AuditService.ROOM_UPDATED, r.getName(),
                (old.equals(r.getName()) ? "" : old + " → " + r.getName() + " · ")
                        + (r.isActive() ? "đang dùng" : "ngừng dùng"));
        return roomView(r);
    }

    /**
     * Nhập nhiều phòng một lần; dòng hợp lệ được tạo, dòng lỗi/trùng chỉ báo lại. Phòng đã có trong danh mục
     * (trùng tên) không bị ghi đè.
     */
    @Transactional
    public RoomImportReport importRooms(String content, AuthPrincipal me) {
        if (content == null || content.isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Content is empty");
        }
        RoomImportParser.Result parsed;
        try {
            parsed = new RoomImportParser(MAX_IMPORT_LINES).parse(content);
        } catch (RoomImportParser.TooManyLinesException e) {
            throw new AppException(ErrorCode.PAYLOAD_TOO_LARGE, e.getMessage());
        }
        List<RoomImportParser.LineIssue> duplicates = new ArrayList<>(parsed.duplicates());
        List<Room> created = new ArrayList<>();
        for (RoomImportParser.Row row : parsed.rows()) {
            // So trùng bằng DB để theo đúng collation của cột UNIQUE rooms.name
            if (rooms.existsByName(row.name())) {
                duplicates.add(new RoomImportParser.LineIssue(row.line(), row.name(), "ALREADY_EXISTS"));
                continue;
            }
            created.add(new Room(row.name(), row.building(), row.capacity(), row.note()));
        }
        rooms.saveAll(created);
        duplicates.sort(Comparator.comparingInt(RoomImportParser.LineIssue::line));
        if (!created.isEmpty()) {
            audit.record(me.userId(), AuditService.ROOMS_IMPORTED, created.size() + " phòng",
                    created.stream().map(Room::getName).limit(20).collect(Collectors.joining(", "))
                            + (created.size() > 20 ? ", …" : ""));
        }
        return new RoomImportReport(parsed.dataLines(), created.stream().map(ScheduleService::roomView).toList(),
                duplicates, parsed.errors());
    }

    /** Chỉ xóa phòng chưa từng được xếp lịch; phòng đã dùng thì ngừng dùng (active = false). */
    @Transactional
    public void deleteRoom(String roomId, AuthPrincipal me) {
        Room r = requireRoom(roomId);
        if (schedules.existsByRoomId(r.getId()) || meetings.existsByRoomId(r.getId())) {
            throw new AppException(ErrorCode.ROOM_IN_USE);
        }
        rooms.delete(r);
        audit.record(me.userId(), AuditService.ROOM_DELETED, r.getName(), null);
    }

    // ================================================================ quy tắc lặp (admin)

    /** Quy tắc của một khóa học hoặc một phòng (null = tất cả), theo thứ rồi tiết. */
    @Transactional(readOnly = true)
    public List<ScheduleView> listSchedules(String courseId, String roomId) {
        Long room = roomId == null || roomId.isBlank() ? null : requireRoom(roomId).getId();
        List<CourseSchedule> list;
        if (courseId != null && !courseId.isBlank()) {
            list = schedules.findByCourseIdInOrderByWeekdayAscStartPeriodAsc(List.of(requireCourse(courseId).getId()));
            if (room != null) {
                list = list.stream().filter(s -> s.getRoomId().equals(room)).toList();
            }
        } else if (room != null) {
            list = schedules.findByRoomIdOrderByWeekdayAscStartPeriodAsc(room);
        } else {
            list = schedules.findAllByOrderByWeekdayAscStartPeriodAsc();
        }
        return scheduleViews(list);
    }

    /** Tạo quy tắc và sinh mọi buổi trong khoảng ngày (cả ngày đã qua: nhập lịch khi học kỳ đã bắt đầu). */
    @Transactional
    public ScheduleResult createSchedule(ScheduleRequest req, AuthPrincipal me) {
        if (req.courseId() == null || req.courseId().isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "courseId is required");
        }
        Course c = requireCourse(req.courseId());
        Room room = requireActiveRoom(req.roomId(), null);
        validateRule(req);
        List<LocalDate> dates = ScheduleRules.weeklyDates(req.weekday(), req.startsOn(), req.endsOn());
        if (dates.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "NO_MEETINGS");
        }
        checkConflicts(c, room.getId(), dates, req.startPeriod(), req.periods(), null);

        CourseSchedule s = new CourseSchedule(c.getId(), me.userId());
        apply(s, req, room);
        schedules.save(s);
        meetings.saveAll(dates.stream().map(d -> newMeeting(s, d)).toList());
        String rule = describeRule(s, room);
        audit.record(me.userId(), AuditService.SCHEDULE_CREATED, c.displayCode(),
                rule + " · " + dates.size() + " buổi");
        if (req.notifyStudents()) {
            notifications.scheduleChanged(c, "Lịch học " + c.displayCode(), rule, me.userId());
        }
        return new ScheduleResult(scheduleView(s, c, room, dates.size()), dates.size(), 0);
    }

    /**
     * Sửa quy tắc: chỉ thay các buổi từ hôm nay trở đi chưa mở điểm danh (kể cả buổi đã dời/hủy riêng);
     * buổi đã qua và buổi đã điểm danh giữ nguyên.
     */
    @Transactional
    public ScheduleResult updateSchedule(String scheduleId, ScheduleRequest req, AuthPrincipal me) {
        CourseSchedule s = requireSchedule(scheduleId);
        Course c = courses.findById(s.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        Room room = requireActiveRoom(req.roomId(), s.getRoomId());
        validateRule(req);
        String before = describeRule(s, rooms.findById(s.getRoomId()).orElse(room));

        LocalDate today = ScheduleRules.today();
        deleteEvidenceAfterCommit(leaveFiles.storageKeysOfUnattendedFrom(s.getId(), today));
        int removed = meetings.deleteUnattendedFrom(s.getId(), today);
        Set<LocalDate> kept = meetings.findByScheduleId(s.getId()).stream().map(ClassMeeting::getDate)
                .filter(d -> !d.isBefore(today)).collect(Collectors.toSet());
        LocalDate from = req.startsOn().isBefore(today) ? today : req.startsOn();
        List<LocalDate> dates = ScheduleRules.weeklyDates(req.weekday(), from, req.endsOn()).stream()
                .filter(d -> !kept.contains(d)).toList();
        checkConflicts(c, room.getId(), dates, req.startPeriod(), req.periods(), null);

        apply(s, req, room);
        meetings.saveAll(dates.stream().map(d -> newMeeting(s, d)).toList());
        String after = describeRule(s, room);
        audit.record(me.userId(), AuditService.SCHEDULE_UPDATED, c.displayCode(),
                before + " → " + after + " · thêm " + dates.size() + ", bỏ " + removed + " buổi");
        if (req.notifyStudents()) {
            notifications.scheduleChanged(c, "Đổi lịch học " + c.displayCode(),
                    "Từ " + DATE.format(from) + ": " + after, me.userId());
        }
        long count = countBySchedule(List.of(s.getId())).getOrDefault(s.getId(), 0L);
        return new ScheduleResult(scheduleView(s, c, room, count), dates.size(), removed);
    }

    /** Xóa quy tắc cùng các buổi từ hôm nay chưa điểm danh; buổi đã qua giữ lại thành buổi lẻ. */
    @Transactional
    public void deleteSchedule(String scheduleId, AuthPrincipal me) {
        CourseSchedule s = requireSchedule(scheduleId);
        Course c = courses.findById(s.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        Room room = rooms.findById(s.getRoomId()).orElseThrow(() -> AppException.notFound("Room"));
        deleteEvidenceAfterCommit(leaveFiles.storageKeysOfUnattendedFrom(s.getId(), ScheduleRules.today()));
        int removed = meetings.deleteUnattendedFrom(s.getId(), ScheduleRules.today());
        schedules.delete(s);
        audit.record(me.userId(), AuditService.SCHEDULE_DELETED, c.displayCode(),
                describeRule(s, room) + " · bỏ " + removed + " buổi");
    }

    // ================================================================ từng buổi (admin)

    /** Thêm buổi lẻ: buổi bù, buổi học thêm. */
    @Transactional
    public MeetingView createMeeting(MeetingRequest req, AuthPrincipal me) {
        if (req.courseId() == null || req.courseId().isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "courseId is required");
        }
        Course c = requireCourse(req.courseId());
        Room room = requireActiveRoom(req.roomId(), null);
        validateSlot(req.startPeriod(), req.periods());
        checkConflicts(c, room.getId(), List.of(req.date()), req.startPeriod(), req.periods(), null);
        ClassMeeting m = new ClassMeeting(c.getId(), null, room.getId(), req.date(), req.startPeriod(),
                req.periods(), req.kind() == null ? CourseSchedule.KIND_THEORY : req.kind());
        m.setNote(CourseService.blankToNull(req.note()));
        meetings.save(m);
        String when = describe(m, room);
        audit.record(me.userId(), AuditService.MEETING_CREATED, c.displayCode(), when);
        if (req.notifyStudents()) {
            notifications.scheduleChanged(c, "Buổi học bù " + c.displayCode(), withNote(when, m.getNote()),
                    me.userId());
        }
        return meetingView(m, c, room, me);
    }

    /** Dời buổi: đổi ngày, tiết, số tiết, phòng. Buổi đã mở điểm danh không dời được. */
    @Transactional
    public MeetingView moveMeeting(String meetingId, MeetingRequest req, AuthPrincipal me) {
        ClassMeeting m = requireMeeting(meetingId);
        requireNoAttendance(m);
        Course c = courses.findById(m.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        Room oldRoom = rooms.findById(m.getRoomId()).orElseThrow(() -> AppException.notFound("Room"));
        Room room = requireActiveRoom(req.roomId(), m.getRoomId());
        validateSlot(req.startPeriod(), req.periods());
        if (!m.isCancelled()) {
            checkConflicts(c, room.getId(), List.of(req.date()), req.startPeriod(), req.periods(), m.getId());
        }
        String before = describe(m, oldRoom);
        m.moveTo(req.date(), req.startPeriod(), req.periods(), room.getId());
        if (req.kind() != null) {
            m.setKind(req.kind());
        }
        if (req.note() != null) {
            m.setNote(CourseService.blankToNull(req.note()));
        }
        String after = describe(m, room);
        audit.record(me.userId(), AuditService.MEETING_MOVED, c.displayCode(), before + " → " + after);
        if (req.notifyStudents() && !m.isCancelled()) {
            notifications.scheduleChanged(c, "Đổi lịch học " + c.displayCode(),
                    withNote(before + " chuyển sang " + after, m.getNote()), me.userId());
        }
        return meetingView(m, c, room, me);
    }

    @Transactional
    public MeetingView cancelMeeting(String meetingId, CancelRequest req, AuthPrincipal me) {
        ClassMeeting m = requireMeeting(meetingId);
        requireNoAttendance(m);
        Course c = courses.findById(m.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        Room room = rooms.findById(m.getRoomId()).orElseThrow(() -> AppException.notFound("Room"));
        if (req.note() != null) {
            m.setNote(CourseService.blankToNull(req.note()));
        }
        if (!m.isCancelled()) {
            m.setCancelled(true);
            String when = describe(m, room);
            audit.record(me.userId(), AuditService.MEETING_CANCELLED, c.displayCode(), when);
            if (req.notifyStudents()) {
                notifications.scheduleChanged(c, "Nghỉ học " + c.displayCode(), withNote(when, m.getNote()),
                        me.userId());
            }
        }
        return meetingView(m, c, room, me);
    }

    /** Khôi phục buổi đã hủy (kiểm tra trùng lại vì chỗ trống có thể đã được xếp lớp khác). */
    @Transactional
    public MeetingView restoreMeeting(String meetingId, CancelRequest req, AuthPrincipal me) {
        ClassMeeting m = requireMeeting(meetingId);
        Course c = courses.findById(m.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        Room room = rooms.findById(m.getRoomId()).orElseThrow(() -> AppException.notFound("Room"));
        if (m.isCancelled()) {
            checkConflicts(c, room.getId(), List.of(m.getDate()), m.getStartPeriod(), m.getPeriods(), m.getId());
            m.setCancelled(false);
            if (req.note() != null) {
                m.setNote(CourseService.blankToNull(req.note()));
            }
            String when = describe(m, room);
            audit.record(me.userId(), AuditService.MEETING_RESTORED, c.displayCode(), when);
            if (req.notifyStudents()) {
                notifications.scheduleChanged(c, "Học lại theo lịch " + c.displayCode(), withNote(when, m.getNote()),
                        me.userId());
            }
        }
        return meetingView(m, c, room, me);
    }

    /** Xóa hẳn một buổi (nhập nhầm); không thông báo. Buổi đã mở điểm danh không xóa được. */
    @Transactional
    public void deleteMeeting(String meetingId, AuthPrincipal me) {
        ClassMeeting m = requireMeeting(meetingId);
        requireNoAttendance(m);
        Course c = courses.findById(m.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        Room room = rooms.findById(m.getRoomId()).orElseThrow(() -> AppException.notFound("Room"));
        deleteEvidenceAfterCommit(leaveFiles.storageKeysByMeetings(List.of(m.getId())));
        meetings.delete(m);
        audit.record(me.userId(), AuditService.MEETING_DELETED, c.displayCode(), describe(m, room));
    }

    // ================================================================ lịch của người xem

    /**
     * Buổi học và mốc mở/đóng bài trong [from, to]. Sinh viên: khóa học đã ghi danh; giảng viên: khóa mình phụ trách
     * (và khóa mình ghi danh nếu có); admin: mọi khóa học (không kèm mốc bài trừ khi lọc theo khóa học).
     * courseId / roomId lọc thêm trong phạm vi đó.
     */
    @Transactional(readOnly = true)
    public ScheduleFeed feed(AuthPrincipal me, LocalDate from, LocalDate to, String courseId, String roomId) {
        if (from == null || to == null || to.isBefore(from) || ChronoUnit.DAYS.between(from, to) >= MAX_FEED_DAYS) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid date range");
        }
        boolean admin = me.role() == Role.ADMIN;
        boolean courseFilter = courseId != null && !courseId.isBlank();
        Long roomFilter = roomId == null || roomId.isBlank() ? null : requireRoom(roomId).getId();

        Map<Long, Course> scope = new LinkedHashMap<>();
        if (admin) {
            if (courseFilter) {
                Course c = requireCourse(courseId);
                scope.put(c.getId(), c);
            }
        } else {
            courses.findByTeacherNewestFirst(me.userId()).forEach(c -> scope.put(c.getId(), c));
            courses.findEnrolledBy(me.userId()).forEach(c -> scope.putIfAbsent(c.getId(), c));
            if (courseFilter) {
                Course c = scope.values().stream().filter(x -> x.getPublicId().equals(courseId)).findFirst()
                        .orElseThrow(() -> AppException.notFound("Course"));
                scope.clear();
                scope.put(c.getId(), c);
            }
        }

        List<ClassMeeting> list;
        if (admin && !courseFilter) {
            list = meetings.findAllInRange(from, to);
            Set<Long> missing = list.stream().map(ClassMeeting::getCourseId).filter(id -> !scope.containsKey(id))
                    .collect(Collectors.toSet());
            courses.findAllById(missing).forEach(c -> scope.put(c.getId(), c));
        } else {
            list = scope.isEmpty() ? List.of() : meetings.findInRange(scope.keySet(), from, to);
        }
        if (roomFilter != null) {
            list = list.stream().filter(m -> m.getRoomId().equals(roomFilter)).toList();
        }
        Map<Long, Room> roomById = roomsOf(list.stream().map(ClassMeeting::getRoomId).toList());
        List<MeetingView> views = meetingViews(list, scope, roomById, me);

        List<DeadlineView> deadlines = new ArrayList<>();
        if ((!admin || courseFilter) && roomFilter == null && !scope.isEmpty()) {
            Instant start = from.atStartOfDay(ScheduleRules.ZONE).toInstant();
            Instant end = to.plusDays(1).atStartOfDay(ScheduleRules.ZONE).toInstant();
            for (Assessment a : assessments.findByCourseIdInOrderByCreatedAt(scope.keySet())) {
                CourseRef course = courseRef(scope.get(a.getCourseId()));
                if (within(a.getOpensAt(), start, end)) {
                    deadlines.add(new DeadlineView(a.getPublicId(), course, a.getType(), a.getTitle(), "opens",
                            a.getOpensAt()));
                }
                if (within(a.getClosesAt(), start, end)) {
                    deadlines.add(new DeadlineView(a.getPublicId(), course, a.getType(), a.getTitle(), "closes",
                            a.getClosesAt()));
                }
            }
            deadlines.sort((x, y) -> x.at().compareTo(y.at()));
        }
        return new ScheduleFeed(rules, from, to, views, deadlines);
    }

    // ================================================================ điểm danh từ buổi học (giảng viên)

    /**
     * Mở buổi điểm danh gắn với buổi học (tên "Buổi N · dd/MM", N = thứ tự buổi chưa hủy của khóa học).
     * Buổi hôm nay thì mở QR luôn; buổi đã qua chỉ để điểm danh thủ công. Gọi lại thì trả buổi đã có.
     */
    @Transactional
    public AttendanceLink openAttendance(String courseId, String meetingId, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        ClassMeeting m = meetings.findByPublicIdAndCourseId(meetingId, c.getId())
                .orElseThrow(() -> AppException.notFound("Meeting"));
        if (m.getAttendanceSessionId() != null) {
            var existing = attendanceSessions.findById(m.getAttendanceSessionId());
            if (existing.isPresent()) {
                return new AttendanceLink(existing.get().getPublicId());
            }
        }
        if (m.isCancelled()) {
            throw new AppException(ErrorCode.MEETING_CANCELLED);
        }
        LocalDate today = ScheduleRules.today();
        if (m.getDate().isAfter(today)) {
            throw new AppException(ErrorCode.MEETING_NOT_STARTED);
        }
        long number = meetings.countUpTo(c.getId(), m.getDate(), m.getStartPeriod());
        String title = "Buổi " + number + " · " + SHORT_DATE.format(m.getDate());
        SessionDetail created = attendance.create(courseId, new CreateSessionRequest(title, m.getDate().equals(today)),
                me);
        AttendanceSession s = attendanceSessions.findByPublicId(created.session().id())
                .orElseThrow(() -> new IllegalStateException("Attendance session missing after create"));
        m.setAttendanceSessionId(s.getId());
        // Sinh viên có đơn xin nghỉ đã được đồng ý: ghi "có phép" ngay khi mở buổi điểm danh
        leaves.applyApproved(m, s.getId(), me.userId());
        return new AttendanceLink(s.getPublicId());
    }

    // ================================================================ nội bộ

    /**
     * Buổi mới (các ngày dates, cùng tiết) không được chung tiết với buổi chưa hủy cùng phòng, cùng khóa học hoặc
     * cùng giảng viên phụ trách. ignoreMeetingId: buổi đang dời/khôi phục.
     */
    private void checkConflicts(Course c, long roomId, Collection<LocalDate> dates, int startPeriod, int periods,
            Long ignoreMeetingId) {
        List<Conflict> conflicts = findConflicts(c, roomId, dates, startPeriod, periods, ignoreMeetingId);
        if (!conflicts.isEmpty()) {
            throw new AppException(ErrorCode.SCHEDULE_CONFLICT, ErrorCode.SCHEDULE_CONFLICT.defaultMessage(),
                    conflicts);
        }
    }

    /**
     * Các buổi đã có (tối đa MAX_CONFLICTS) trùng với buổi mới; rỗng = xếp được. Dùng chung với nhập thời khóa
     * biểu.
     */
    List<Conflict> findConflicts(Course c, long roomId, Collection<LocalDate> dates, int startPeriod, int periods,
            Long ignoreMeetingId) {
        if (dates.isEmpty()) {
            return List.of();
        }
        Long teacherId = c.getTeacher() == null ? null : c.getTeacher().getId();
        List<ClassMeeting> found = meetings.findPossibleConflicts(new HashSet<>(dates), roomId, c.getId(), teacherId)
                .stream()
                .filter(m -> !m.getId().equals(ignoreMeetingId))
                .filter(m -> ScheduleRules.overlaps(m.getDate(), m.getStartPeriod(), m.getPeriods(), m.getDate(),
                        startPeriod, periods))
                .limit(MAX_CONFLICTS)
                .toList();
        if (found.isEmpty()) {
            return List.of();
        }
        Map<Long, Course> courseById = courses.findAllById(found.stream().map(ClassMeeting::getCourseId).toList())
                .stream().collect(Collectors.toMap(Course::getId, Function.identity()));
        Map<Long, Room> roomById = roomsOf(found.stream().map(ClassMeeting::getRoomId).toList());
        return found.stream().map(m -> new Conflict(m.getDate(), m.getStartPeriod(), m.getPeriods(),
                m.getRoomId() == roomId ? "room" : m.getCourseId().equals(c.getId()) ? "course" : "teacher",
                courseRef(courseById.get(m.getCourseId())), roomRef(roomById.get(m.getRoomId())))).toList();
    }

    /** Đơn xin nghỉ của các buổi bị xóa đi theo FK cascade; file minh chứng trên đĩa xóa sau khi commit. */
    private void deleteEvidenceAfterCommit(List<String> keys) {
        if (!keys.isEmpty()) {
            CourseService.afterCommit(() -> keys.forEach(storage::deleteQuietly));
        }
    }

    private void validateRule(ScheduleRequest req) {
        validateSlot(req.startPeriod(), req.periods());
        if (req.endsOn().isBefore(req.startsOn())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "INVALID_RANGE");
        }
        if (ChronoUnit.DAYS.between(req.startsOn(), req.endsOn()) >= ScheduleRules.MAX_RULE_DAYS) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "RANGE_TOO_LONG");
        }
    }

    private void validateSlot(int startPeriod, int periods) {
        if (!rules.isValidSlot(startPeriod, periods)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "INVALID_PERIODS");
        }
    }

    private static void apply(CourseSchedule s, ScheduleRequest req, Room room) {
        s.setRoomId(room.getId());
        s.setWeekday(req.weekday());
        s.setStartPeriod(req.startPeriod());
        s.setPeriods(req.periods());
        s.setKind(req.kind());
        s.setStartsOn(req.startsOn());
        s.setEndsOn(req.endsOn());
    }

    static ClassMeeting newMeeting(CourseSchedule s, LocalDate date) {
        return new ClassMeeting(s.getCourseId(), s.getId(), s.getRoomId(), date, s.getStartPeriod(), s.getPeriods(),
                s.getKind());
    }

    private Course requireCourse(String courseId) {
        return courses.findByPublicId(courseId).orElseThrow(() -> AppException.notFound("Course"));
    }

    private Room requireRoom(String roomId) {
        return rooms.findByPublicId(roomId).orElseThrow(() -> AppException.notFound("Room"));
    }

    /** Phòng phải đang dùng, trừ khi giữ nguyên phòng hiện tại (currentRoomId) của quy tắc/buổi đang sửa. */
    private Room requireActiveRoom(String roomId, Long currentRoomId) {
        Room r = requireRoom(roomId);
        if (!r.isActive() && !r.getId().equals(currentRoomId)) {
            throw new AppException(ErrorCode.ROOM_INACTIVE);
        }
        return r;
    }

    private CourseSchedule requireSchedule(String scheduleId) {
        return schedules.findByPublicId(scheduleId).orElseThrow(() -> AppException.notFound("Schedule"));
    }

    private ClassMeeting requireMeeting(String meetingId) {
        return meetings.findByPublicId(meetingId).orElseThrow(() -> AppException.notFound("Meeting"));
    }

    /** Buổi đã mở điểm danh (và buổi điểm danh còn) thì không dời, hủy, xóa. */
    private void requireNoAttendance(ClassMeeting m) {
        if (m.getAttendanceSessionId() != null && attendanceSessions.existsById(m.getAttendanceSessionId())) {
            throw new AppException(ErrorCode.MEETING_HAS_ATTENDANCE);
        }
    }

    private Map<Long, Room> roomsOf(Collection<Long> ids) {
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        return distinct.isEmpty() ? Map.of()
                : rooms.findAllById(distinct).stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }

    private Map<Long, Long> countBySchedule(Collection<Long> ids) {
        return ids.isEmpty() ? Map.of()
                : meetings.countBySchedule(ids).stream()
                        .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
    }

    private List<ScheduleView> scheduleViews(List<CourseSchedule> list) {
        if (list.isEmpty()) {
            return List.of();
        }
        Map<Long, Course> courseById = courses.findAllById(list.stream().map(CourseSchedule::getCourseId).toList())
                .stream().collect(Collectors.toMap(Course::getId, Function.identity()));
        Map<Long, Room> roomById = roomsOf(list.stream().map(CourseSchedule::getRoomId).toList());
        Map<Long, Long> counts = countBySchedule(list.stream().map(CourseSchedule::getId).toList());
        return list.stream().map(s -> scheduleView(s, courseById.get(s.getCourseId()), roomById.get(s.getRoomId()),
                counts.getOrDefault(s.getId(), 0L))).toList();
    }

    private static ScheduleView scheduleView(CourseSchedule s, Course c, Room room, long meetingCount) {
        return new ScheduleView(s.getPublicId(), courseRef(c), roomRef(room), s.getWeekday(), s.getStartPeriod(),
                s.getPeriods(), s.getKind(), s.getStartsOn(), s.getEndsOn(), meetingCount);
    }

    private MeetingView meetingView(ClassMeeting m, Course c, Room room, AuthPrincipal me) {
        return meetingViews(List.of(m), Map.of(c.getId(), c), Map.of(room.getId(), room), me).get(0);
    }

    /** Dựng nhiều buổi một lúc: id quy tắc và buổi điểm danh lấy bằng một truy vấn mỗi loại. */
    private List<MeetingView> meetingViews(List<ClassMeeting> list, Map<Long, Course> courseById,
            Map<Long, Room> roomById, AuthPrincipal me) {
        Map<Long, String> scheduleIds = schedules
                .findAllById(list.stream().map(ClassMeeting::getScheduleId).filter(Objects::nonNull).distinct()
                        .toList())
                .stream().collect(Collectors.toMap(CourseSchedule::getId, CourseSchedule::getPublicId));
        Map<Long, String> sessionIds = attendanceSessions
                .findAllById(list.stream().map(ClassMeeting::getAttendanceSessionId).filter(Objects::nonNull)
                        .distinct().toList())
                .stream().collect(Collectors.toMap(AttendanceSession::getId, AttendanceSession::getPublicId));
        List<Long> meetingIds = list.stream().map(ClassMeeting::getId).toList();
        Map<Long, MyLeave> myLeaves = meetingIds.isEmpty() ? Map.of()
                : leaveRequests.findByUserIdAndMeetingIdIn(me.userId(), meetingIds).stream()
                        .collect(Collectors.toMap(LeaveRequest::getMeetingId,
                                r -> new MyLeave(r.getPublicId(), r.getStatus(), r.getTeacherNote())));
        boolean admin = me.role() == Role.ADMIN;
        List<Long> managedIds = list.stream()
                .filter(m -> courseById.get(m.getCourseId()).isManagedBy(me.userId(), admin))
                .map(ClassMeeting::getId).toList();
        Map<Long, Long> pending = managedIds.isEmpty() ? Map.of()
                : leaveRequests.countPendingByMeeting(managedIds).stream()
                        .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
        return list.stream().map(m -> {
            Course c = courseById.get(m.getCourseId());
            boolean manager = c.isManagedBy(me.userId(), admin);
            return new MeetingView(m.getPublicId(), courseRef(c), roomRef(roomById.get(m.getRoomId())),
                    m.getScheduleId() == null ? null : scheduleIds.get(m.getScheduleId()), m.getDate(),
                    m.getStartPeriod(), m.getPeriods(), rules.startsAt(m.getDate(), m.getStartPeriod()),
                    rules.endsAt(m.getDate(), m.getStartPeriod(), m.getPeriods()), m.getKind(), m.isCancelled(),
                    m.getOriginalDate(), m.getOriginalStartPeriod(), m.getNote(), CourseService.isTeacherOf(c, me),
                    manager && m.getAttendanceSessionId() != null ? sessionIds.get(m.getAttendanceSessionId()) : null,
                    LeaveService.deadline(rules, c, m), myLeaves.get(m.getId()),
                    pending.getOrDefault(m.getId(), 0L).intValue());
        }).toList();
    }

    static CourseRef courseRef(Course c) {
        return c == null ? null
                : new CourseRef(c.getPublicId(), c.displayCode(), c.getTitle(),
                        c.getTeacher() == null ? null : c.getTeacher().getFullName());
    }

    static RoomRef roomRef(Room r) {
        return r == null ? null : new RoomRef(r.getPublicId(), r.getName(), r.getBuilding());
    }

    private static RoomView roomView(Room r) {
        return new RoomView(r.getPublicId(), r.getName(), r.getBuilding(), r.getCapacity(), r.getNote(),
                r.isActive());
    }

    private static boolean within(Instant t, Instant start, Instant end) {
        return t != null && !t.isBefore(start) && t.isBefore(end);
    }

    /** "Thứ 3, tiết 1–3, phòng 402-A2, 08/09/2026 – 15/12/2026" (nhật ký, thông báo). */
    static String describeRule(CourseSchedule s, Room room) {
        return WEEKDAYS[s.getWeekday() - 1] + ", " + periodLabel(s.getStartPeriod(), s.getPeriods()) + ", phòng "
                + room.getName() + ", " + DATE.format(s.getStartsOn()) + " – " + DATE.format(s.getEndsOn());
    }

    /** "Thứ 5 30/10/2026, tiết 1–3, phòng 402-A2". */
    static String describe(ClassMeeting m, Room room) {
        return WEEKDAYS[m.getDate().getDayOfWeek().getValue() - 1] + " " + DATE.format(m.getDate()) + ", "
                + periodLabel(m.getStartPeriod(), m.getPeriods()) + ", phòng " + room.getName();
    }

    static String periodLabel(int startPeriod, int periods) {
        return periods == 1 ? "tiết " + startPeriod : "tiết " + startPeriod + "–" + (startPeriod + periods - 1);
    }

    private static String withNote(String text, String note) {
        return note == null ? text : text + ". " + note;
    }
}
