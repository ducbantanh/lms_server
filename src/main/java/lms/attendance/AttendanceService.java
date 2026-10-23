package lms.attendance;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.attendance.AttendanceViews.AttendanceOverview;
import lms.attendance.AttendanceViews.AttendanceRow;
import lms.attendance.AttendanceViews.CheckInResult;
import lms.attendance.AttendanceViews.CheckedIn;
import lms.attendance.AttendanceViews.CreateSessionRequest;
import lms.attendance.AttendanceViews.MarkRequest;
import lms.attendance.AttendanceViews.MyAttendance;
import lms.attendance.AttendanceViews.QrCode;
import lms.attendance.AttendanceViews.SessionDetail;
import lms.attendance.AttendanceViews.SessionSummary;
import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.EnrollmentRepository;
import lms.realtime.RealtimeEvent;
import lms.realtime.RealtimeHub;
import lms.report.ReportService;
import lms.user.User;

/**
 * Điểm danh theo buổi. Giảng viên phụ trách tạo buổi, chiếu mã QR (AttendanceQr, đổi mỗi 10 giây) cho sinh viên
 * quét, và điểm danh thủ công cho người không quét được. Admin chỉ xem (TEACHER_ONLY như chấm điểm).
 * Danh sách luôn là sinh viên đang ghi danh; không có dòng điểm danh = vắng.
 */
@Service
public class AttendanceService {

    /** Ngày trong tên cột file CSV theo múi giờ của trường. */
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    /** Theo AttendanceRecord.STATUS_*; vắng là không có dòng. */
    private static final String[] STATUS_LABELS = { "Có mặt", "Đi muộn", "Có phép" };
    private static final String ABSENT_LABEL = "Vắng";

    private final AttendanceSessionRepository sessions;
    private final AttendanceRecordRepository records;
    private final CourseRepository courses;
    private final CourseService courseService;
    private final EnrollmentRepository enrollments;
    private final RealtimeHub hub;
    private final AuditService audit;

    public AttendanceService(AttendanceSessionRepository sessions, AttendanceRecordRepository records,
            CourseRepository courses, CourseService courseService, EnrollmentRepository enrollments, RealtimeHub hub,
            AuditService audit) {
        this.sessions = sessions;
        this.records = records;
        this.courses = courses;
        this.courseService = courseService;
        this.enrollments = enrollments;
        this.hub = hub;
        this.audit = audit;
    }

    // ================================================================ giảng viên

    @Transactional(readOnly = true)
    public AttendanceOverview overview(String courseId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        List<AttendanceSession> list = sessions.findByCourseIdOrderByCreatedAtDescIdDesc(c.getId());
        return new AttendanceOverview(enrolled(c).size(), summaries(c, list));
    }

    @Transactional
    public SessionDetail create(String courseId, CreateSessionRequest req, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        String title = CourseService.blankToNull(req.title());
        if (title == null) {
            title = "Buổi " + (sessions.countByCourseId(c.getId()) + 1);
        }
        AttendanceSession s = sessions.save(new AttendanceSession(c.getId(), title, req.qr(), me.userId()));
        audit.record(me.userId(), AuditService.ATTENDANCE_SESSION_CREATED, c.displayCode(), title);
        return detail(c, s);
    }

    @Transactional(readOnly = true)
    public SessionDetail detail(String courseId, String sessionId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        return detail(c, requireSession(c, sessionId));
    }

    @Transactional
    public SessionDetail rename(String courseId, String sessionId, String title, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        AttendanceSession s = requireSession(c, sessionId);
        String old = s.getTitle();
        s.setTitle(title.trim());
        audit.record(me.userId(), AuditService.ATTENDANCE_SESSION_UPDATED, c.displayCode(), old + " → " + s.getTitle());
        return detail(c, s);
    }

    /** Mở / dừng điểm danh bằng QR; dừng rồi thì mã đang chiếu (kể cả ảnh chụp) hết dùng được ngay. */
    @Transactional
    public SessionDetail setQrOpen(String courseId, String sessionId, boolean open, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        AttendanceSession s = requireSession(c, sessionId);
        if (s.isQrOpen() != open) {
            s.setQrOpen(open);
            audit.record(me.userId(), open ? AuditService.ATTENDANCE_QR_OPENED : AuditService.ATTENDANCE_QR_CLOSED,
                    c.displayCode(), s.getTitle());
        }
        return detail(c, s);
    }

    @Transactional
    public void delete(String courseId, String sessionId, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        AttendanceSession s = requireSession(c, sessionId);
        sessions.delete(s);
        audit.record(me.userId(), AuditService.ATTENDANCE_SESSION_DELETED, c.displayCode(), s.getTitle());
    }

    /** Mã QR của chu kỳ hiện tại cho màn chiếu; chỉ giảng viên phụ trách, khi buổi đang mở QR. */
    @Transactional(readOnly = true)
    public QrCode qr(String courseId, String sessionId, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        AttendanceSession s = requireSession(c, sessionId);
        if (!s.isQrOpen()) {
            throw new AppException(ErrorCode.ATTENDANCE_CLOSED);
        }
        Map<Long, User> students = enrolled(c);
        int checkedIn = (int) records.findBySessionId(s.getId()).stream()
                .filter(r -> students.containsKey(r.getUserId()) && r.getStatus() != AttendanceRecord.STATUS_EXCUSED)
                .count();
        Instant now = Instant.now();
        long period = AttendanceQr.period(now);
        return new QrCode(c.displayCode(), s.getTitle(), AttendanceQr.token(s.getPublicId(), s.getQrSecret(), period),
                AttendanceQr.periodEnd(period), now, AttendanceQr.PERIOD_SECONDS, checkedIn, students.size());
    }

    /**
     * Điểm danh thủ công cho sinh viên đang ghi danh (id không thuộc lớp bị bỏ qua); status null = vắng.
     * Người đã có đúng trạng thái đó thì giữ nguyên dòng cũ (vd. vẫn ghi là tự quét QR).
     */
    @Transactional
    public SessionDetail mark(String courseId, String sessionId, MarkRequest req, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        AttendanceSession s = requireSession(c, sessionId);
        Integer status = req.status();
        if (status != null && !AttendanceRecord.isValidStatus(status)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Unknown attendance status");
        }
        Map<String, User> byPublicId = enrolled(c).values().stream()
                .collect(Collectors.toMap(User::getPublicId, Function.identity()));
        List<User> targets = req.studentIds().stream().distinct().map(byPublicId::get).filter(Objects::nonNull)
                .toList();
        if (targets.isEmpty()) {
            throw AppException.notFound("Student");
        }
        List<Long> ids = targets.stream().map(User::getId).toList();
        if (status == null) {
            records.deleteSome(s.getId(), ids);
        } else {
            Map<Long, AttendanceRecord> existing = records.findBySessionIdAndUserIdIn(s.getId(), ids).stream()
                    .collect(Collectors.toMap(AttendanceRecord::getUserId, Function.identity()));
            for (Long userId : ids) {
                AttendanceRecord r = existing.get(userId);
                if (r == null) {
                    records.save(AttendanceRecord.manual(s.getId(), userId, status, me.userId()));
                } else if (r.getStatus() != status) {
                    r.markManually(status, me.userId());
                }
            }
        }
        String who = targets.size() == 1 ? targets.get(0).getUsername() : targets.size() + " sinh viên";
        audit.record(me.userId(), AuditService.ATTENDANCE_MARKED, c.displayCode(),
                s.getTitle() + " · " + who + " → " + statusLabel(status));
        return detail(c, s);
    }

    /**
     * Bảng điểm danh: mỗi sinh viên đang ghi danh một dòng, mỗi buổi một cột (cũ trước), cuối là số buổi
     * theo từng trạng thái. UTF-8 có BOM như bảng điểm để Excel đọc đúng tiếng Việt.
     */
    @Transactional(readOnly = true)
    public ReportService.Csv csv(String courseId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        List<AttendanceSession> list = sessions.findByCourseIdOrderByCreatedAtAscIdAsc(c.getId());
        Map<String, Integer> statusOf = list.isEmpty() ? Map.of()
                : records.findBySessionIdIn(list.stream().map(AttendanceSession::getId).toList()).stream()
                        .collect(Collectors.toMap(r -> r.getSessionId() + ":" + r.getUserId(),
                                AttendanceRecord::getStatus));

        List<String> header = new ArrayList<>(List.of("MSSV", "Họ tên"));
        list.forEach(s -> header.add(s.getTitle() + " (" + DATE.format(s.getCreatedAt()) + ")"));
        header.addAll(List.of(STATUS_LABELS));
        header.add(ABSENT_LABEL);
        StringBuilder out = new StringBuilder("﻿").append(ReportService.csvLine(header));
        Map<Long, User> students = enrolled(c);
        for (User u : students.values()) {
            List<String> cells = new ArrayList<>(List.of(u.getUsername(), u.getFullName()));
            int[] totals = new int[STATUS_LABELS.length + 1];
            for (AttendanceSession s : list) {
                Integer status = statusOf.get(s.getId() + ":" + u.getId());
                cells.add(statusLabel(status));
                totals[status == null ? STATUS_LABELS.length : status]++;
            }
            for (int total : totals) {
                cells.add(Integer.toString(total));
            }
            out.append(ReportService.csvLine(cells));
        }
        audit.record(me.userId(), AuditService.ATTENDANCE_EXPORTED, c.displayCode(),
                students.size() + " sinh viên, " + list.size() + " buổi");
        return new ReportService.Csv("diem-danh-" + ReportService.fileSafe(c.displayCode()) + ".csv",
                out.toString().getBytes(StandardCharsets.UTF_8));
    }

    // ================================================================ sinh viên

    /**
     * Sinh viên quét mã QR trên màn chiếu. Thứ tự kiểm tra: đúng mã của chu kỳ hiện tại/ngay trước → buổi còn mở QR
     * → có tên trong lớp. Quét lại khi đã có trạng thái (kể cả do giảng viên điểm) không đổi gì.
     */
    @Transactional
    public CheckInResult checkIn(String token, AuthPrincipal me, String ip) {
        AttendanceQr.Parsed parsed = AttendanceQr.parse(token)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_CODE_INVALID));
        AttendanceSession s = sessions.findByPublicId(parsed.sessionId())
                .filter(x -> AttendanceQr.isValid(parsed, x.getQrSecret(), AttendanceQr.period(Instant.now())))
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_CODE_INVALID));
        if (!s.isQrOpen()) {
            throw new AppException(ErrorCode.ATTENDANCE_CLOSED);
        }
        Course c = courses.findById(s.getCourseId()).orElseThrow(() -> AppException.notFound("Course"));
        if (!enrollments.existsByCourseIdAndUserId(c.getId(), me.userId())) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }
        boolean inserted = records.insertScanned(s.getId(), me.userId(), ip) == 1;
        AttendanceRecord r = records.findById(new AttendanceRecord.Key(s.getId(), me.userId()))
                .orElseThrow(() -> new IllegalStateException("Attendance record missing after insert"));
        if (inserted) {
            audit.record(me.userId(), AuditService.ATTENDANCE_CHECKED_IN, c.displayCode(), s.getTitle(), ip);
            if (c.getTeacher() != null) {
                long teacherId = c.getTeacher().getId();
                CheckedIn event = new CheckedIn(c.getPublicId(), s.getPublicId(), new AttendanceRow(me.publicId(),
                        me.username(), me.fullName(), r.getStatus(), r.getMethod(), r.getCheckedAt(), r.getIp()));
                CourseService.afterCommit(() -> hub.sendToUser(teacherId,
                        RealtimeEvent.of(RealtimeEvent.ATTENDANCE_CHECKED_IN, event)));
            }
        }
        return new CheckInResult(c.getPublicId(), c.displayCode(), c.getTitle(), s.getTitle(), r.getStatus(),
                r.getCheckedAt(), !inserted);
    }

    @Transactional(readOnly = true)
    public List<MyAttendance> mine(String courseId, AuthPrincipal me) {
        Course c = courses.findByPublicId(courseId)
                .filter(x -> enrollments.existsByCourseIdAndUserId(x.getId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Course"));
        List<AttendanceSession> list = sessions.findByCourseIdOrderByCreatedAtAscIdAsc(c.getId());
        Map<Long, AttendanceRecord> bySession = list.isEmpty() ? Map.of()
                : records.findBySessionIdInAndUserId(list.stream().map(AttendanceSession::getId).toList(),
                        me.userId()).stream()
                        .collect(Collectors.toMap(AttendanceRecord::getSessionId, Function.identity()));
        return list.stream().map(s -> {
            AttendanceRecord r = bySession.get(s.getId());
            return new MyAttendance(s.getPublicId(), s.getTitle(), s.getCreatedAt(),
                    r == null ? null : r.getStatus(), r == null ? null : r.getCheckedAt());
        }).toList();
    }

    // ================================================================ nội bộ

    /** Sinh viên đang ghi danh theo id user, thứ tự MSSV. */
    private Map<Long, User> enrolled(Course c) {
        Map<Long, User> result = new LinkedHashMap<>();
        for (Object[] row : enrollments.findWithUsers(c.getId())) {
            User u = (User) row[1];
            result.put(u.getId(), u);
        }
        return result;
    }

    private AttendanceSession requireSession(Course c, String sessionId) {
        return sessions.findByPublicIdAndCourseId(sessionId, c.getId())
                .orElseThrow(() -> AppException.notFound("Attendance session"));
    }

    private SessionDetail detail(Course c, AttendanceSession s) {
        Map<Long, User> students = enrolled(c);
        Map<Long, AttendanceRecord> byUser = records.findBySessionId(s.getId()).stream()
                .collect(Collectors.toMap(AttendanceRecord::getUserId, Function.identity()));
        int[] counts = new int[STATUS_LABELS.length];
        List<AttendanceRow> rows = new ArrayList<>(students.size());
        for (User u : students.values()) {
            AttendanceRecord r = byUser.get(u.getId());
            if (r != null) {
                counts[r.getStatus()]++;
            }
            rows.add(new AttendanceRow(u.getPublicId(), u.getUsername(), u.getFullName(),
                    r == null ? null : r.getStatus(), r == null ? null : r.getMethod(),
                    r == null ? null : r.getCheckedAt(), r == null ? null : r.getIp()));
        }
        return new SessionDetail(summary(s, counts), students.size(), rows);
    }

    /** Số đếm của nhiều buổi bằng một truy vấn. */
    private List<SessionSummary> summaries(Course c, List<AttendanceSession> list) {
        if (list.isEmpty()) {
            return List.of();
        }
        Map<Long, int[]> counts = new LinkedHashMap<>();
        for (Object[] row : records.countByStatus(c.getId(), list.stream().map(AttendanceSession::getId).toList())) {
            int status = ((Number) row[1]).intValue();
            if (AttendanceRecord.isValidStatus(status)) {
                counts.computeIfAbsent((Long) row[0], k -> new int[STATUS_LABELS.length])[status] =
                        ((Number) row[2]).intValue();
            }
        }
        return list.stream().map(s -> summary(s, counts.getOrDefault(s.getId(), new int[STATUS_LABELS.length])))
                .toList();
    }

    private static SessionSummary summary(AttendanceSession s, int[] counts) {
        return new SessionSummary(s.getPublicId(), s.getTitle(), s.isQrOpen(), s.getCreatedAt(),
                counts[AttendanceRecord.STATUS_PRESENT], counts[AttendanceRecord.STATUS_LATE],
                counts[AttendanceRecord.STATUS_EXCUSED]);
    }

    static String statusLabel(Integer status) {
        return status == null ? ABSENT_LABEL : STATUS_LABELS[status];
    }
}
