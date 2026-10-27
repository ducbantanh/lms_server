package lms.leave;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.attendance.AttendanceRecord;
import lms.attendance.AttendanceRecordRepository;
import lms.attendance.AttendanceSessionRepository;
import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.EnrollmentRepository;
import lms.course.FileStorage;
import lms.course.UploadChecker;
import lms.leave.LeaveViews.CourseLeaves;
import lms.leave.LeaveViews.FileView;
import lms.leave.LeaveViews.LeaveView;
import lms.leave.LeaveViews.MeetingRef;
import lms.leave.LeaveViews.StudentRef;
import lms.notification.NotificationService;
import lms.schedule.ClassMeeting;
import lms.schedule.ClassMeetingRepository;
import lms.schedule.Room;
import lms.schedule.RoomRepository;
import lms.schedule.ScheduleRules;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Đơn xin nghỉ buổi học. Sinh viên đang ghi danh viết lý do, đính kèm minh chứng (ảnh, PDF) cho một buổi chưa hủy,
 * chậm nhất Course.leaveNoticeHours giờ trước giờ bắt đầu; đơn đang chờ thì rút được. Giảng viên phụ trách duyệt:
 * đồng ý thì sinh viên được điểm danh "có phép" ở buổi đó (ngay nếu buổi đã mở điểm danh, hoặc khi mở sau này qua
 * ScheduleService.openAttendance); duyệt lại thành từ chối thì bỏ trạng thái có phép đã ghi.
 */
@Service
public class LeaveService {

    /** Tối đa 30 ngày báo trước. */
    public static final int MAX_NOTICE_HOURS = 720;
    static final int MAX_FILES = 3;
    static final int MIN_REASON = 5;
    static final int MAX_REASON = 1000;
    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ScheduleRules rules;
    private final LeaveRequestRepository requests;
    private final LeaveRequestFileRepository files;
    private final ClassMeetingRepository meetings;
    private final RoomRepository rooms;
    private final CourseRepository courses;
    private final CourseService courseService;
    private final EnrollmentRepository enrollments;
    private final UserRepository users;
    private final AttendanceRecordRepository attendanceRecords;
    private final AttendanceSessionRepository attendanceSessions;
    private final FileStorage storage;
    private final UploadChecker uploads;
    private final NotificationService notifications;
    private final AuditService audit;

    public LeaveService(AppProperties props, LeaveRequestRepository requests, LeaveRequestFileRepository files,
            ClassMeetingRepository meetings, RoomRepository rooms, CourseRepository courses,
            CourseService courseService, EnrollmentRepository enrollments, UserRepository users,
            AttendanceRecordRepository attendanceRecords, AttendanceSessionRepository attendanceSessions,
            FileStorage storage, UploadChecker uploads, NotificationService notifications, AuditService audit) {
        this.rules = new ScheduleRules(props.schedule().firstPeriodHour(), props.schedule().periodsPerDay());
        this.requests = requests;
        this.files = files;
        this.meetings = meetings;
        this.rooms = rooms;
        this.courses = courses;
        this.courseService = courseService;
        this.enrollments = enrollments;
        this.users = users;
        this.attendanceRecords = attendanceRecords;
        this.attendanceSessions = attendanceSessions;
        this.storage = storage;
        this.uploads = uploads;
        this.notifications = notifications;
        this.audit = audit;
    }

    /** Hạn gửi đơn: giờ bắt đầu buổi trừ số giờ giảng viên quy định. */
    public static Instant deadline(ScheduleRules rules, Course c, ClassMeeting m) {
        return rules.startsAt(m.getDate(), m.getStartPeriod()).minus(Duration.ofHours(c.getLeaveNoticeHours()));
    }

    // ================================================================ sinh viên

    @Transactional
    public LeaveView submit(String meetingId, String reason, List<MultipartFile> uploaded, AuthPrincipal me) {
        ClassMeeting m = meetings.findByPublicId(meetingId).orElseThrow(() -> AppException.notFound("Meeting"));
        Course c = courses.findById(m.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        if (!enrollments.existsByCourseIdAndUserId(c.getId(), me.userId())) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }
        if (m.isCancelled()) {
            throw new AppException(ErrorCode.MEETING_CANCELLED);
        }
        Instant deadline = deadline(rules, c, m);
        if (Instant.now().isAfter(deadline)) {
            throw new AppException(ErrorCode.LEAVE_DEADLINE_PASSED, ErrorCode.LEAVE_DEADLINE_PASSED.defaultMessage(),
                    Map.of("deadline", deadline, "noticeHours", c.getLeaveNoticeHours()));
        }
        if (requests.existsByMeetingIdAndUserId(m.getId(), me.userId())) {
            throw new AppException(ErrorCode.LEAVE_ALREADY_REQUESTED);
        }
        String text = reason == null ? "" : reason.strip();
        if (text.length() < MIN_REASON || text.length() > MAX_REASON) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "INVALID_REASON");
        }
        List<MultipartFile> parts = uploaded == null ? List.of()
                : uploaded.stream().filter(f -> f != null && !f.isEmpty()).toList();
        if (parts.size() > MAX_FILES) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "TOO_MANY_FILES");
        }
        List<UploadChecker.Checked> checked = parts.stream().map(f -> uploads.check(f, UploadChecker.EVIDENCE))
                .toList();

        LeaveRequest r = requests.save(new LeaveRequest(m.getId(), c.getId(), me.userId(), text));
        List<String> stored = new ArrayList<>();
        try {
            for (UploadChecker.Checked f : checked) {
                String key = storage.store(f.data());
                stored.add(key);
                files.save(new LeaveRequestFile(r.getId(), f.name(), f.contentType(), f.data().length, key));
            }
        } catch (RuntimeException e) {
            stored.forEach(storage::deleteQuietly);
            throw e;
        }
        audit.record(me.userId(), AuditService.LEAVE_REQUESTED, c.displayCode(),
                WHEN.format(m.getDate()) + " · " + checked.size() + " minh chứng");
        return views(List.of(r)).get(0);
    }

    /** Rút đơn đang chờ duyệt (xóa cả file minh chứng). */
    @Transactional
    public void withdraw(String requestId, AuthPrincipal me) {
        LeaveRequest r = requests.findByPublicId(requestId).filter(x -> x.getUserId() == me.userId())
                .orElseThrow(() -> AppException.notFound("Leave request"));
        if (!r.isPending()) {
            throw new AppException(ErrorCode.LEAVE_ALREADY_REVIEWED);
        }
        List<LeaveRequestFile> evidence = files.findByRequestId(r.getId());
        requests.delete(r);
        CourseService.afterCommit(() -> evidence.forEach(f -> storage.deleteQuietly(f.getStorageKey())));
        Course c = courses.findById(r.getCourseId()).orElse(null);
        audit.record(me.userId(), AuditService.LEAVE_WITHDRAWN, c == null ? null : c.displayCode(), null);
    }

    // ================================================================ giảng viên

    @Transactional(readOnly = true)
    public CourseLeaves forCourse(String courseId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        return new CourseLeaves(c.getLeaveNoticeHours(), MAX_NOTICE_HOURS,
                views(requests.findForCourse(c.getId())));
    }

    /** Đồng ý / từ chối (duyệt lại được). Đồng bộ trạng thái "có phép" nếu buổi đã mở điểm danh; báo sinh viên. */
    @Transactional
    public LeaveView review(String courseId, String requestId, boolean approve, String note, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        LeaveRequest r = requests.findByPublicId(requestId).filter(x -> x.getCourseId().equals(c.getId()))
                .orElseThrow(() -> AppException.notFound("Leave request"));
        ClassMeeting m = meetings.findById(r.getMeetingId()).orElseThrow(() -> AppException.notFound("Meeting"));
        r.review(approve, CourseService.blankToNull(note), me.userId());
        if (m.getAttendanceSessionId() != null && attendanceSessions.existsById(m.getAttendanceSessionId())) {
            syncAttendance(m.getAttendanceSessionId(), r.getUserId(), approve, me.userId());
        }
        String when = WHEN.format(m.getDate());
        notifications.leaveReviewed(c, r.getUserId(),
                (approve ? "Đã duyệt đơn xin nghỉ " : "Không duyệt đơn xin nghỉ ") + c.displayCode(),
                "Buổi " + when + (r.getTeacherNote() == null ? "" : ". " + r.getTeacherNote()), me.userId());
        String student = users.findById(r.getUserId()).map(User::getUsername).orElse("?");
        audit.record(me.userId(), AuditService.LEAVE_REVIEWED, c.displayCode(),
                student + " · " + when + " → " + (approve ? "đồng ý" : "từ chối"));
        return views(List.of(r)).get(0);
    }

    @Transactional
    public int updateNoticeHours(String courseId, int hours, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        if (hours < 0 || hours > MAX_NOTICE_HOURS) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "INVALID_HOURS");
        }
        int old = c.getLeaveNoticeHours();
        c.setLeaveNoticeHours(hours);
        audit.record(me.userId(), AuditService.LEAVE_SETTINGS_UPDATED, c.displayCode(), old + " → " + hours + " giờ");
        return hours;
    }

    /**
     * Buổi điểm danh vừa mở từ buổi học: sinh viên có đơn đã được đồng ý được ghi "có phép" (trừ khi đã có trạng
     * thái). Gọi từ ScheduleService.openAttendance trong cùng transaction.
     */
    public void applyApproved(ClassMeeting m, long sessionId, long teacherId) {
        for (LeaveRequest r : requests.findByMeetingIdAndStatus(m.getId(), LeaveRequest.STATUS_APPROVED)) {
            syncAttendance(sessionId, r.getUserId(), true, teacherId);
        }
    }

    // ================================================================ tải minh chứng

    public record Download(LeaveRequestFile file, Path path) {
    }

    /** Người viết đơn hoặc người quản lý khóa học (giảng viên phụ trách, admin). */
    @Transactional(readOnly = true)
    public Download download(String fileId, AuthPrincipal me) {
        LeaveRequestFile f = files.findByPublicId(fileId).orElseThrow(() -> AppException.notFound("File"));
        LeaveRequest r = requests.findById(f.getRequestId()).orElseThrow(() -> AppException.notFound("File"));
        boolean allowed = r.getUserId() == me.userId() || courses.findById(r.getCourseId())
                .map(c -> c.isManagedBy(me.userId(), me.role() == Role.ADMIN)).orElse(false);
        if (!allowed) {
            throw AppException.notFound("File");
        }
        return new Download(f, storage.path(f.getStorageKey()));
    }

    // ================================================================ nội bộ

    /** Đồng ý: thêm "có phép" nếu sinh viên chưa có trạng thái. Từ chối: bỏ "có phép" đã ghi thủ công. */
    private void syncAttendance(long sessionId, long userId, boolean approve, long teacherId) {
        var existing = attendanceRecords.findById(new AttendanceRecord.Key(sessionId, userId));
        if (approve && existing.isEmpty()) {
            attendanceRecords.save(AttendanceRecord.manual(sessionId, userId, AttendanceRecord.STATUS_EXCUSED,
                    teacherId));
        } else if (!approve && existing.filter(x -> x.getStatus() == AttendanceRecord.STATUS_EXCUSED).isPresent()) {
            attendanceRecords.deleteSome(sessionId, List.of(userId));
        }
    }

    /** Dựng nhiều đơn: buổi, phòng, sinh viên, người duyệt, file bằng một truy vấn mỗi loại. */
    private List<LeaveView> views(List<LeaveRequest> list) {
        if (list.isEmpty()) {
            return List.of();
        }
        Map<Long, ClassMeeting> meetingById = meetings.findAllById(ids(list, LeaveRequest::getMeetingId)).stream()
                .collect(Collectors.toMap(ClassMeeting::getId, Function.identity()));
        Map<Long, String> roomNames = rooms.findAllById(meetingById.values().stream().map(ClassMeeting::getRoomId)
                .distinct().toList()).stream().collect(Collectors.toMap(Room::getId, Room::getName));
        List<Long> userIds = new ArrayList<>(ids(list, LeaveRequest::getUserId));
        userIds.addAll(ids(list, LeaveRequest::getReviewedBy));
        Map<Long, User> userById = users.findAllById(userIds.stream().distinct().toList()).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, List<FileView>> filesByRequest = files.findByRequestIdIn(ids(list, LeaveRequest::getId)).stream()
                .collect(Collectors.groupingBy(LeaveRequestFile::getRequestId, Collectors.mapping(
                        f -> new FileView(f.getPublicId(), f.getName(), f.getContentType(), f.getSize()),
                        Collectors.toList())));
        return list.stream().map(r -> {
            ClassMeeting m = meetingById.get(r.getMeetingId());
            User u = userById.get(r.getUserId());
            User reviewer = r.getReviewedBy() == null ? null : userById.get(r.getReviewedBy());
            return new LeaveView(r.getPublicId(),
                    new MeetingRef(m.getPublicId(), m.getDate(), m.getStartPeriod(), m.getPeriods(),
                            roomNames.get(m.getRoomId()), rules.startsAt(m.getDate(), m.getStartPeriod()),
                            m.isCancelled()),
                    u == null ? null : new StudentRef(u.getPublicId(), u.getUsername(), u.getFullName()),
                    r.getReason(), r.getStatus(), r.getTeacherNote(), reviewer == null ? null : reviewer.getFullName(),
                    r.getReviewedAt(), r.getCreatedAt(), filesByRequest.getOrDefault(r.getId(), List.of()));
        }).toList();
    }

    private static List<Long> ids(Collection<LeaveRequest> list, Function<LeaveRequest, Long> getter) {
        return list.stream().map(getter).filter(Objects::nonNull).distinct().toList();
    }
}
