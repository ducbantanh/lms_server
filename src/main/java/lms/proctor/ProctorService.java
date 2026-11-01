package lms.proctor;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseFile;
import lms.course.CourseFileRepository;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.EnrollmentRepository;
import lms.proctor.ProctorViews.AccessCode;
import lms.proctor.ProctorViews.AssessmentRef;
import lms.proctor.ProctorViews.Board;
import lms.proctor.ProctorViews.BoardRow;
import lms.proctor.ProctorViews.BoardSummary;
import lms.proctor.ProctorViews.ClientEvent;
import lms.proctor.ProctorViews.CourseRef;
import lms.proctor.ProctorViews.EventView;
import lms.proctor.ProctorViews.InExam;
import lms.proctor.ProctorViews.ReportRequest;
import lms.proctor.ProctorViews.RowChanged;
import lms.proctor.ProctorViews.Settings;
import lms.proctor.ProctorViews.SettingsRequest;
import lms.proctor.ProctorViews.StateChanged;
import lms.proctor.ProctorViews.StudentLog;
import lms.proctor.ProctorViews.StudentProctor;
import lms.realtime.RealtimeEvent;
import lms.realtime.RealtimeRelay;
import lms.submission.Submission;
import lms.submission.SubmissionFile;
import lms.submission.SubmissionFileRepository;
import lms.submission.SubmissionRepository;
import lms.submission.SubmissionService;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Giám sát bài kiểm tra (Assessment.TYPE_EXAM, mọi hình thức) khi giảng viên bật. Trong giờ làm bài:
 * <ul>
 * <li>sinh viên vào phòng thi (start): kiểm tra mạng (IP / dải CIDR), mã phòng thi đổi mỗi phút; phiên thi gắn với
 * phiên đăng nhập lúc vào, máy khác (phiên đăng nhập khác) bị chặn tới khi giám thị cho đổi máy;</li>
 * <li>mọi API làm bài (đề, câu hỏi, nộp) phải qua phiên thi (ProctorInterceptor gọi checkWork), và sinh viên đang thi
 * chỉ dùng được trang làm bài (các API khác trả PROCTOR_IN_EXAM);</li>
 * <li>trình duyệt báo rời trang, thoát toàn màn hình, dán từ ngoài vào (report); server tính vi phạm
 * (ProctorRules), đủ số lần giảng viên đặt thì khóa bài chờ giám thị mở;</li>
 * <li>nộp bài (trắc nghiệm, nộp file) hoặc bấm kết thúc (lập trình) thì phiên thi kết thúc.</li>
 * </ul>
 * Trình duyệt có thể bị qua mặt (tắt JavaScript, dùng máy khác ngoài phòng): phần chặn ở server (mạng, mã phòng,
 * một máy, chỉ trang làm bài) là chắc chắn; phần trình duyệt báo là để răn đe và làm bằng chứng cho giám thị.
 */
@Service
public class ProctorService {

    /** Nhập sai mã phòng thi: tối đa bấy nhiêu lần trong CODE_WINDOW cho mỗi sinh viên ở mỗi bài. */
    static final int MAX_CODE_FAILURES = 5;
    private static final Duration CODE_WINDOW = Duration.ofMinutes(5);
    /** Gọi API làm bài không ghi last_seen_at quá dày (nhịp 15 giây đã cập nhật). */
    private static final Duration TOUCH_INTERVAL = Duration.ofSeconds(10);

    /** Bài sinh viên đang thi (ProctorInterceptor). */
    public record ActiveExam(long assessmentId, String publicId, String coursePublicId, Long paperFileId,
            String title) {
    }

    private final AssessmentRepository assessments;
    private final CourseRepository courses;
    private final CourseService courseService;
    private final EnrollmentRepository enrollments;
    private final CourseFileRepository courseFiles;
    private final SubmissionRepository submissions;
    private final SubmissionFileRepository submissionFiles;
    private final UserRepository users;
    private final ProctorSessionRepository sessions;
    private final ProctorEventRepository events;
    private final AuditService audit;
    private final RealtimeRelay relay;
    private final Map<String, Deque<Instant>> codeFailures = new ConcurrentHashMap<>();

    public ProctorService(AssessmentRepository assessments, CourseRepository courses, CourseService courseService,
            EnrollmentRepository enrollments, CourseFileRepository courseFiles, SubmissionRepository submissions,
            SubmissionFileRepository submissionFiles, UserRepository users, ProctorSessionRepository sessions,
            ProctorEventRepository events, AuditService audit, RealtimeRelay relay) {
        this.assessments = assessments;
        this.courses = courses;
        this.courseService = courseService;
        this.enrollments = enrollments;
        this.courseFiles = courseFiles;
        this.submissions = submissions;
        this.submissionFiles = submissionFiles;
        this.users = users;
        this.sessions = sessions;
        this.events = events;
        this.audit = audit;
        this.relay = relay;
    }

    /** Giờ lưu vào cột DATETIME(3): trả về đúng giá trị đọc lại sau này. */
    private static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public static boolean isProctoredExam(Assessment a) {
        return a.isProctored() && a.getType() == Assessment.TYPE_EXAM;
    }

    // ================================================================ sinh viên

    /** Trạng thái phòng thi cho trang làm bài; null nếu bài không phải bài kiểm tra có giám sát. */
    @Transactional(readOnly = true)
    public StudentProctor studentState(Assessment a, AuthPrincipal me, String ip) {
        if (!isProctoredExam(a)) {
            return null;
        }
        return state(a, sessions.findByAssessmentIdAndUserId(a.getId(), me.userId()).orElse(null), me, ip);
    }

    @Transactional(readOnly = true)
    public StudentProctor studentState(String assessmentId, AuthPrincipal me, String ip) {
        return studentState(requireEnrolledExam(assessmentId, me), me, ip);
    }

    /** Đề (file PDF) của bài có giám sát chỉ hiện trong phòng thi, ở máy đang làm bài. */
    @Transactional(readOnly = true)
    public boolean paperAllowed(Assessment a, AuthPrincipal me) {
        if (!a.isProctoredAt(now())) {
            return true;
        }
        return sessions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .filter(s -> s.isActive() && s.isBoundTo(me.sessionId())).isPresent();
    }

    /**
     * Vào phòng thi: lần đầu tạo phiên thi; giám thị vừa cho đổi máy thì gắn máy này; đang ở máy này thì trả về
     * trạng thái như cũ. Máy khác đang làm bài → PROCTOR_OTHER_DEVICE (ghi nhật ký cho giám thị).
     */
    @Transactional(noRollbackFor = AppException.class)
    public StudentProctor start(String assessmentId, String accessCode, AuthPrincipal me, String ip,
            String userAgent) {
        Assessment a = requireEnrolledExam(assessmentId, me);
        Instant now = now();
        if (!a.isOpened(now)) {
            throw new AppException(ErrorCode.ASSESSMENT_NOT_OPEN);
        }
        if (a.isClosed(now)) {
            throw new AppException(ErrorCode.SUBMISSION_LOCKED);
        }
        ProctorSession s = sessions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .flatMap(x -> sessions.lockById(x.getId())).orElse(null);
        if (s != null) {
            if (s.isFinished()) {
                throw new AppException(ErrorCode.PROCTOR_FINISHED);
            }
            if (s.isBoundTo(me.sessionId())) {
                return state(a, s, me, ip);
            }
            if (s.getAuthSessionId() != null) {
                record(s, ProctorRules.OTHER_DEVICE, false, null, "Thử vào thi từ máy khác", ip, null, now);
                notifyTeacher(a, s, now);
                throw new AppException(ErrorCode.PROCTOR_OTHER_DEVICE);
            }
        }
        if (!networks(a).allows(ip)) {
            throw new AppException(ErrorCode.PROCTOR_NETWORK_DENIED,
                    ErrorCode.PROCTOR_NETWORK_DENIED.defaultMessage(), Map.of("ip", ip == null ? "" : ip));
        }
        if (a.isProctorAccessCode()) {
            checkAccessCode(a, s, accessCode, me, ip, now);
        }
        if (s == null) {
            s = sessions.save(new ProctorSession(a.getId(), a.getCourseId(), me.userId(), me.sessionId(), ip,
                    userAgent, now));
            record(s, ProctorRules.STARTED, false, null, null, ip, null, now);
            audit.record(me.userId(), AuditService.PROCTOR_STARTED, courseCode(a), a.getTitle(), ip);
        } else {
            s.bind(me.sessionId(), ip, userAgent, now);
            record(s, ProctorRules.DEVICE_CHANGED, false, null, userAgent, ip, null, now);
        }
        notifyTeacher(a, s, now);
        return state(a, s, me, ip);
    }

    /**
     * Nhịp của trang làm bài (15 giây một lần, hoặc ngay khi có sự kiện): trạng thái rời trang / toàn màn hình và
     * các sự kiện trình duyệt ghi được. Đủ số lần vi phạm thì khóa bài.
     */
    @Transactional
    public StudentProctor report(String assessmentId, ReportRequest req, AuthPrincipal me, String ip) {
        Assessment a = requireEnrolledExam(assessmentId, me);
        Instant now = now();
        ProctorSession s = sessions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .orElseThrow(() -> new AppException(ErrorCode.PROCTOR_REQUIRED));
        if (!s.isBoundTo(me.sessionId())) {
            throw new AppException(
                    s.getAuthSessionId() == null ? ErrorCode.PROCTOR_REQUIRED : ErrorCode.PROCTOR_OTHER_DEVICE);
        }
        if (s.isFinished() || !a.isProctoredAt(now)) {
            return state(a, s, me, ip);
        }
        s = sessions.lockById(s.getId()).orElseThrow(() -> new AppException(ErrorCode.PROCTOR_REQUIRED));
        boolean wasAway = s.getAwaySince() != null;
        boolean wasFullscreen = s.isFullscreen();
        s.clientState(req.away(), req.fullscreen(), now);
        boolean changed = wasAway != (s.getAwaySince() != null) || wasFullscreen != s.isFullscreen();
        changed |= noteIp(s, ip, now);
        s.seen(ip, now);
        List<ClientEvent> reported = req.events() == null ? List.of() : req.events();
        long logged = reported.isEmpty() ? 0 : events.countBySessionId(s.getId());
        for (ClientEvent e : reported) {
            if (!ProctorRules.CLIENT_TYPES.contains(e.type())) {
                continue;
            }
            boolean counted = ProctorRules.counted(e.type(), e.durationMs(), e.detail(), a.isProctorFullscreen());
            if (logged < ProctorRules.MAX_EVENTS) {
                record(s, e.type(), counted, e.durationMs(), e.detail(), ip, null,
                        ProctorRules.occurredAt(e.at(), s.getStartedAt(), now));
                logged++;
            }
            if (counted) {
                s.addViolation();
            }
            changed = true;
        }
        if (lockIfLimit(a, s, now)) {
            notifyStudent(a, s);
        }
        if (changed) {
            notifyTeacher(a, s, now);
        }
        return state(a, s, me, ip);
    }

    /** Bài lập trình: sinh viên bấm "Kết thúc bài thi", không chạy / nộp được nữa và rời phòng thi. */
    @Transactional
    public StudentProctor finish(String assessmentId, AuthPrincipal me, String ip) {
        Assessment a = requireEnrolledExam(assessmentId, me);
        if (!a.isCode()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Finish by submitting the exam");
        }
        Instant now = now();
        ProctorSession s = sessions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .filter(x -> x.isBoundTo(me.sessionId()))
                .flatMap(x -> sessions.lockById(x.getId()))
                .orElseThrow(() -> new AppException(ErrorCode.PROCTOR_REQUIRED));
        if (s.isLocked()) {
            throw new AppException(ErrorCode.PROCTOR_LOCKED);
        }
        if (!s.isFinished()) {
            s.finish(now);
            record(s, ProctorRules.FINISHED, false, null, "finished", ip, null, now);
            notifyTeacher(a, s, now);
        }
        return state(a, s, me, ip);
    }

    /**
     * Bài làm đã chốt (trắc nghiệm nộp / hết giờ, nộp file): kết thúc phiên thi nếu có. Gọi trong transaction của
     * nghiệp vụ nộp bài; bài không có giám sát thì không làm gì.
     */
    @Transactional
    public void onWorkFinished(Assessment a, long userId, String reason) {
        if (!isProctoredExam(a)) {
            return;
        }
        sessions.findByAssessmentIdAndUserId(a.getId(), userId).filter(s -> !s.isFinished()).ifPresent(s -> {
            Instant now = now();
            s.finish(now);
            record(s, ProctorRules.FINISHED, false, null, reason, null, null, now);
            notifyTeacher(a, s, now);
        });
    }

    // ================================================================ ProctorInterceptor

    /** Bài kiểm tra có giám sát sinh viên đang thi (phiên chưa kết thúc, bài còn trong giờ). */
    @Transactional(readOnly = true)
    public Optional<ActiveExam> inExam(long userId) {
        List<Object[]> rows = sessions.findInExam(userId, now());
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        Assessment a = (Assessment) rows.get(0)[1];
        String course = courses.findById(a.getCourseId()).map(Course::getPublicId).orElse(null);
        return Optional.of(new ActiveExam(a.getId(), a.getPublicId(), course, a.getPaperFileId(), a.getTitle()));
    }

    public static AppException inExamError(ActiveExam exam) {
        return new AppException(ErrorCode.PROCTOR_IN_EXAM, ErrorCode.PROCTOR_IN_EXAM.defaultMessage(),
                new InExam(exam.publicId(), exam.coursePublicId(), exam.title()));
    }

    /**
     * API làm bài (/student/assessments/{id}/quiz|code|files|submit) của bài đang trong giờ thi có giám sát: phải có
     * phiên thi ở máy này, đúng mạng, không bị khóa. Đã kết thúc thì chỉ còn xem (GET). Bài khác, chưa ghi danh:
     * để controller xử lý như thường.
     */
    @Transactional(noRollbackFor = AppException.class)
    public void checkWork(String assessmentId, AuthPrincipal me, String ip, boolean readOnly) {
        Instant now = now();
        Assessment a = assessments.findByPublicId(assessmentId).orElse(null);
        if (a == null || !a.isProctoredAt(now)
                || !enrollments.existsByCourseIdAndUserId(a.getCourseId(), me.userId())) {
            return;
        }
        ProctorSession s = sessions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .orElseThrow(() -> new AppException(ErrorCode.PROCTOR_REQUIRED));
        if (s.isFinished()) {
            if (readOnly) {
                return;
            }
            throw new AppException(ErrorCode.PROCTOR_FINISHED);
        }
        if (!s.isBoundTo(me.sessionId())) {
            throw new AppException(
                    s.getAuthSessionId() == null ? ErrorCode.PROCTOR_REQUIRED : ErrorCode.PROCTOR_OTHER_DEVICE);
        }
        if (!networks(a).allows(ip)) {
            throw new AppException(ErrorCode.PROCTOR_NETWORK_DENIED,
                    ErrorCode.PROCTOR_NETWORK_DENIED.defaultMessage(), Map.of("ip", ip == null ? "" : ip));
        }
        if (s.isLocked()) {
            throw new AppException(ErrorCode.PROCTOR_LOCKED);
        }
        if (!Objects.equals(ip, s.getLastIp())) {
            ProctorSession locked = sessions.lockById(s.getId()).orElseThrow();
            noteIp(locked, ip, now);
            locked.seen(ip, now);
            boolean justLocked = lockIfLimit(a, locked, now);
            notifyTeacher(a, locked, now);
            if (justLocked) {
                notifyStudent(a, locked);
                throw new AppException(ErrorCode.PROCTOR_LOCKED);
            }
        } else if (s.getLastSeenAt().isBefore(now.minus(TOUCH_INTERVAL))) {
            sessions.touch(s.getId(), now);
        }
    }

    /**
     * GET /files/{id}: đề của bài đang trong giờ thi có giám sát chỉ tải được trong phòng thi ở máy đang làm bài;
     * sinh viên đang thi (exam khác null) chỉ tải được đề của bài đang thi.
     */
    @Transactional(readOnly = true)
    public void checkCourseFile(String fileId, AuthPrincipal me, ActiveExam exam) {
        CourseFile f = courseFiles.findByPublicId(fileId).orElse(null);
        if (f == null) {
            if (exam != null) {
                throw inExamError(exam);
            }
            return;
        }
        Instant now = now();
        List<Assessment> guarded = assessments.findByPaperFileId(f.getId()).stream()
                .filter(a -> a.isProctoredAt(now)).toList();
        if (exam != null && guarded.stream().noneMatch(a -> a.getId() == exam.assessmentId())) {
            throw inExamError(exam);
        }
        if (!guarded.isEmpty() && guarded.stream().noneMatch(a -> paperAllowed(a, me))) {
            throw new AppException(ErrorCode.PROCTOR_REQUIRED);
        }
    }

    /** GET /submission-files/{id} khi đang thi: chỉ file bài làm của bài đang thi (danh sách file đã tải lên). */
    @Transactional(readOnly = true)
    public void checkSubmissionFile(String fileId, ActiveExam exam) {
        boolean ofExam = submissionFiles.findByPublicId(fileId).map(SubmissionFile::getSubmissionId)
                .flatMap(submissions::findById).map(Submission::getAssessmentId)
                .filter(id -> id == exam.assessmentId()).isPresent();
        if (!ofExam) {
            throw inExamError(exam);
        }
    }

    // ================================================================ giảng viên

    /** Bảng giám thị: mọi sinh viên đã ghi danh, kể cả chưa vào phòng thi; giảng viên phụ trách và admin xem được. */
    @Transactional(readOnly = true)
    public Board board(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        Assessment a = requireExam(c, assessmentId);
        Instant now = now();
        Map<Long, ProctorSession> byUser = sessions.findByAssessmentId(a.getId()).stream()
                .collect(Collectors.toMap(ProctorSession::getUserId, Function.identity()));
        Map<Long, ProctorEvent> latest = byUser.isEmpty() ? Map.of()
                : events.findLatest(byUser.values().stream().map(ProctorSession::getId).toList()).stream()
                        .collect(Collectors.toMap(ProctorEvent::getSessionId, Function.identity()));
        Map<Long, Submission> work = submissions.findByAssessmentId(a.getId()).stream()
                .collect(Collectors.toMap(Submission::getUserId, Function.identity()));
        Map<Long, Long> fileCounts = !a.usesFiles() || work.isEmpty() ? Map.of()
                : submissionFiles.countBySubmissionIds(work.values().stream().map(Submission::getId).toList())
                        .stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
        Map<Long, String> actors = actorNames(latest.values());
        IpRanges nets = networks(a);
        List<BoardRow> rows = enrollments.findWithUsers(c.getId()).stream().map(r -> {
            User u = (User) r[1];
            ProctorSession s = byUser.get(u.getId());
            Submission sub = work.get(u.getId());
            int files = sub == null ? 0 : fileCounts.getOrDefault(sub.getId(), 0L).intValue();
            return row(a, u, s, sub, files, s == null ? null : latest.get(s.getId()), actors, nets, now);
        }).toList();
        return new Board(new CourseRef(c.getPublicId(), c.displayCode(), c.getTitle()),
                new AssessmentRef(a.getPublicId(), a.getTitle(), a.getAnswerMode(), a.getOpensAt(), a.getClosesAt(),
                        a.statusAt(now)),
                settings(a), isProctoredExam(a) && a.isProctorAccessCode() ? accessCode(a, now) : null,
                summary(rows), rows, CourseService.isTeacherOf(c, me), now);
    }

    /** Mã phòng thi đang hiệu lực (màn chiếu của giám thị lấy mã mới khi hết chu kỳ). */
    @Transactional(readOnly = true)
    public AccessCode currentCode(String courseId, String assessmentId, AuthPrincipal me) {
        Assessment a = requireExam(courseService.requireManaged(courseId, me), assessmentId);
        if (!isProctoredExam(a) || !a.isProctorAccessCode()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Access code is off");
        }
        return accessCode(a, now());
    }

    @Transactional(readOnly = true)
    public StudentLog studentLog(String courseId, String assessmentId, String studentId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        Assessment a = requireExam(c, assessmentId);
        User u = requireStudent(c, studentId);
        ProctorSession s = sessions.findByAssessmentIdAndUserId(a.getId(), u.getId()).orElse(null);
        List<ProctorEvent> list = s == null ? List.of() : events.findBySessionIdOrderByIdAsc(s.getId());
        Map<Long, String> actors = actorNames(list);
        Instant now = now();
        return new StudentLog(row(a, u, s, now), s == null ? null : s.getUserAgent(),
                s == null ? null : s.getStartIp(), list.stream().map(e -> eventView(e, actors)).toList());
    }

    /** Giám thị khóa bài của một sinh viên (vd. bắt gặp dùng điện thoại); sinh viên không làm tiếp được. */
    @Transactional
    public BoardRow lock(String courseId, String assessmentId, String studentId, String reason, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        Assessment a = requireExam(c, assessmentId);
        ProctorSession s = requireSession(a, requireStudent(c, studentId));
        Instant now = now();
        if (s.isActive()) {
            String text = reason == null || reason.isBlank() ? "Giám thị khóa bài" : reason.strip();
            s.lock(text, now);
            record(s, ProctorRules.LOCKED, false, null, text, null, me.userId(), now);
            audit.record(me.userId(), AuditService.PROCTOR_LOCKED, c.displayCode(),
                    a.getTitle() + " · " + username(s) + " (" + text + ")");
            notifyStudent(a, s);
            notifyTeacher(a, s, now);
        }
        return row(a, s, now);
    }

    /** Mở khóa: sinh viên làm tiếp; số vi phạm giữ nguyên nên vi phạm kế tiếp (nếu đã đủ ngưỡng) lại khóa bài. */
    @Transactional
    public BoardRow unlock(String courseId, String assessmentId, String studentId, String note, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        Assessment a = requireExam(c, assessmentId);
        ProctorSession s = requireSession(a, requireStudent(c, studentId));
        Instant now = now();
        if (s.isLocked()) {
            s.unlock();
            String text = note == null || note.isBlank() ? null : note.strip();
            record(s, ProctorRules.UNLOCKED, false, null, text, null, me.userId(), now);
            audit.record(me.userId(), AuditService.PROCTOR_UNLOCKED, c.displayCode(),
                    a.getTitle() + " · " + username(s) + (text == null ? "" : " (" + text + ")"));
            notifyStudent(a, s);
            notifyTeacher(a, s, now);
        }
        return row(a, s, now);
    }

    /**
     * Cho đổi máy: bỏ gắn phiên thi với máy cũ, lần vào phòng thi kế tiếp (từ máy nào cũng được, vẫn kiểm tra mạng và
     * mã phòng) gắn máy đó. Dùng khi máy hỏng, trình duyệt bị đóng phải đăng nhập lại.
     */
    @Transactional
    public BoardRow resetDevice(String courseId, String assessmentId, String studentId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        Assessment a = requireExam(c, assessmentId);
        ProctorSession s = requireSession(a, requireStudent(c, studentId));
        Instant now = now();
        if (!s.isFinished() && s.getAuthSessionId() != null) {
            s.releaseDevice();
            record(s, ProctorRules.DEVICE_RESET, false, null, null, null, me.userId(), now);
            audit.record(me.userId(), AuditService.PROCTOR_DEVICE_RESET, c.displayCode(),
                    a.getTitle() + " · " + username(s));
            notifyStudent(a, s);
            notifyTeacher(a, s, now);
        }
        return row(a, s, now);
    }

    /**
     * Bật / tắt và cài đặt giám sát; chỉ bài kiểm tra, chỉ giảng viên phụ trách. Bật giám sát cần giờ đóng bài: hết
     * giờ thì sinh viên tự ra khỏi chế độ thi.
     */
    @Transactional
    public Settings updateSettings(String courseId, String assessmentId, SettingsRequest req, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        Assessment a = requireExam(c, assessmentId);
        if (a.isLocked(now())) {
            throw new AppException(ErrorCode.ASSESSMENT_CLOSED);
        }
        List<String> invalid = IpRanges.invalidEntries(req.networks());
        if (!invalid.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "INVALID_NETWORKS", Map.of("invalid", invalid));
        }
        if (IpRanges.entries(req.networks()).size() > IpRanges.MAX_ENTRIES) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "TOO_MANY_NETWORKS");
        }
        if (req.enabled() && a.getClosesAt() == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "CLOSE_TIME_REQUIRED");
        }
        a.configureProctor(req.enabled(), IpRanges.normalize(req.networks()), req.accessCode(), req.fullscreen(),
                req.maxViolations());
        Settings result = settings(a);
        audit.record(me.userId(), AuditService.PROCTOR_SETTINGS_UPDATED, c.displayCode(),
                a.getTitle() + " (" + describe(result) + ")");
        return result;
    }

    // ---------------------------------------------------------------- nội bộ

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void cleanupCodeFailures() {
        Instant since = now().minus(CODE_WINDOW);
        codeFailures.values().forEach(q -> {
            synchronized (q) {
                q.removeIf(at -> at.isBefore(since));
            }
        });
        codeFailures.values().removeIf(q -> {
            synchronized (q) {
                return q.isEmpty();
            }
        });
    }

    private void checkAccessCode(Assessment a, ProctorSession s, String input, AuthPrincipal me, String ip,
            Instant now) {
        String key = a.getId() + ":" + me.userId();
        Deque<Instant> failures = codeFailures.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (failures) {
            failures.removeIf(at -> at.isBefore(now.minus(CODE_WINDOW)));
            if (failures.size() >= MAX_CODE_FAILURES) {
                throw new AppException(ErrorCode.TOO_MANY_ATTEMPTS);
            }
            if (ProctorCode.isValid(input, a.getPublicId(), a.getProctorSecret(), ProctorCode.period(now))) {
                failures.clear();
                return;
            }
            failures.add(now);
        }
        if (s != null) {
            record(s, ProctorRules.CODE_REJECTED, false, null, null, ip, null, now);
        }
        throw new AppException(ErrorCode.PROCTOR_CODE_INVALID);
    }

    /** IP đổi giữa giờ thi: ghi nhật ký và tính vi phạm; true nếu đổi. */
    private boolean noteIp(ProctorSession s, String ip, Instant now) {
        if (Objects.equals(ip, s.getLastIp())) {
            return false;
        }
        record(s, ProctorRules.IP_CHANGED, true, null, s.getLastIp() + " → " + ip, ip, null, now);
        s.addViolation();
        return true;
    }

    /** Đủ số lần vi phạm thì khóa bài; true nếu vừa khóa. */
    private boolean lockIfLimit(Assessment a, ProctorSession s, Instant now) {
        if (!s.isActive() || !ProctorRules.reachedLimit(s.getViolations(), a.getProctorMaxViolations())) {
            return false;
        }
        String reason = "Vi phạm " + s.getViolations() + " lần";
        s.lock(reason, now);
        record(s, ProctorRules.LOCKED, false, null, reason, null, null, now);
        audit.record(null, AuditService.PROCTOR_LOCKED, courseCode(a), a.getTitle() + " · " + username(s) + " ("
                + reason + ")");
        return true;
    }

    private void record(ProctorSession s, String type, boolean counted, Long durationMs, String detail, String ip,
            Long actorId, Instant at) {
        events.save(new ProctorEvent(s.getId(), type, counted, durationMs, detail, ip, actorId, at));
    }

    private StudentProctor state(Assessment a, ProctorSession s, AuthPrincipal me, String ip) {
        IpRanges nets = networks(a);
        String status;
        if (s == null) {
            status = ProctorViews.NONE;
        } else if (s.isFinished()) {
            status = ProctorViews.FINISHED;
        } else if (s.getAuthSessionId() == null) {
            status = ProctorViews.NONE;
        } else if (!s.isBoundTo(me.sessionId())) {
            status = ProctorViews.OTHER_DEVICE;
        } else {
            status = s.isLocked() ? ProctorViews.LOCKED : ProctorViews.ACTIVE;
        }
        return new StudentProctor(status, s == null ? 0 : s.getViolations(), a.getProctorMaxViolations(),
                a.isProctorAccessCode(), a.isProctorFullscreen(), !nets.isEmpty(), nets.allows(ip),
                s == null || !s.isLocked() ? null : s.getLockReason(), s == null ? null : s.getStartedAt(),
                a.isCode());
    }

    private static IpRanges networks(Assessment a) {
        try {
            return IpRanges.parse(a.getProctorNetworks());
        } catch (IllegalArgumentException e) {
            // Đã kiểm tra khi lưu; dữ liệu hỏng thì chặn hết còn hơn mở cho mọi mạng
            return IpRanges.parse("0.0.0.0/32");
        }
    }

    private static Settings settings(Assessment a) {
        return new Settings(isProctoredExam(a), a.getProctorNetworks(), a.isProctorAccessCode(),
                a.isProctorFullscreen(), a.getProctorMaxViolations());
    }

    private static String describe(Settings s) {
        if (!s.enabled()) {
            return "tắt giám sát";
        }
        return "giám sát"
                + (s.networks() == null ? "" : ", mạng " + s.networks())
                + (s.accessCode() ? ", mã phòng thi" : "")
                + (s.fullscreen() ? ", toàn màn hình" : "")
                + (s.maxViolations() == null ? ", chỉ ghi vi phạm" : ", khóa khi vi phạm " + s.maxViolations()
                        + " lần");
    }

    private static AccessCode accessCode(Assessment a, Instant now) {
        long period = ProctorCode.period(now);
        return new AccessCode(ProctorCode.code(a.getPublicId(), a.getProctorSecret(), period),
                ProctorCode.periodEnd(period), ProctorCode.PERIOD_SECONDS);
    }

    private Assessment requireEnrolledExam(String assessmentId, AuthPrincipal me) {
        return assessments.findByPublicId(assessmentId)
                .filter(ProctorService::isProctoredExam)
                .filter(x -> enrollments.existsByCourseIdAndUserId(x.getCourseId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Assessment"));
    }

    private Assessment requireExam(Course c, String assessmentId) {
        return assessments.findByPublicIdAndCourseId(assessmentId, c.getId())
                .filter(x -> x.getType() == Assessment.TYPE_EXAM)
                .orElseThrow(() -> AppException.notFound("Assessment"));
    }

    private User requireStudent(Course c, String studentId) {
        return users.findByPublicId(studentId)
                .filter(u -> enrollments.existsByCourseIdAndUserId(c.getId(), u.getId()))
                .orElseThrow(() -> AppException.notFound("Student"));
    }

    private ProctorSession requireSession(Assessment a, User u) {
        return sessions.findByAssessmentIdAndUserId(a.getId(), u.getId())
                .flatMap(s -> sessions.lockById(s.getId()))
                .orElseThrow(() -> AppException.notFound("Proctor session"));
    }

    private String courseCode(Assessment a) {
        return courses.findById(a.getCourseId()).map(Course::displayCode).orElse(null);
    }

    private String username(ProctorSession s) {
        return users.findById(s.getUserId()).map(User::getUsername).orElse("?");
    }

    private Map<Long, String> actorNames(Collection<ProctorEvent> list) {
        List<Long> ids = list.stream().map(ProctorEvent::getActorId).filter(Objects::nonNull).distinct().toList();
        return ids.isEmpty() ? Map.of()
                : users.findAllById(ids).stream().collect(Collectors.toMap(User::getId, User::getFullName));
    }

    private static EventView eventView(ProctorEvent e, Map<Long, String> actors) {
        return new EventView(e.getType(), e.isCounted(), e.getDurationMs(), e.getDetail(), e.getIp(),
                e.getActorId() == null ? null : actors.get(e.getActorId()), e.getOccurredAt());
    }

    /** Một dòng của bảng giám thị, tự tải bài làm và sự kiện gần nhất (sự kiện realtime, thao tác của giám thị). */
    private BoardRow row(Assessment a, ProctorSession s, Instant now) {
        User u = users.findById(s.getUserId()).orElseThrow(() -> AppException.notFound("Student"));
        return row(a, u, s, now);
    }

    private BoardRow row(Assessment a, User u, ProctorSession s, Instant now) {
        Submission sub = submissions.findByAssessmentIdAndUserId(a.getId(), u.getId()).orElse(null);
        int files = sub == null || !a.usesFiles() ? 0 : (int) submissionFiles.countBySubmissionId(sub.getId());
        ProctorEvent last = s == null ? null : events.findLatest(List.of(s.getId())).stream().findFirst().orElse(null);
        return row(a, u, s, sub, files, last, actorNames(last == null ? List.of() : List.of(last)), networks(a), now);
    }

    private static BoardRow row(Assessment a, User u, ProctorSession s, Submission sub, int fileCount,
            ProctorEvent last, Map<Long, String> actors, IpRanges nets, Instant now) {
        String work = SubmissionService.statusOf(a, sub, fileCount, now);
        if (s == null) {
            return new BoardRow(u.getPublicId(), u.getUsername(), u.getFullName(), ProctorViews.NOT_STARTED, work,
                    false, null, false, 0, null, null, null, null, true, null, null);
        }
        String status = s.isFinished() ? ProctorViews.FINISHED
                : s.isLocked() ? ProctorViews.LOCKED : ProctorViews.ACTIVE;
        return new BoardRow(u.getPublicId(), u.getUsername(), u.getFullName(), status, work,
                !s.isFinished() && ProctorRules.isOnline(s.getLastSeenAt(), now), s.getAwaySince(),
                s.isFullscreen(), s.getViolations(), s.getStartedAt(), s.getLastSeenAt(), s.getFinishedAt(),
                s.getLastIp(), nets.allows(s.getLastIp()), s.getLockReason(),
                last == null ? null : eventView(last, actors));
    }

    private static BoardSummary summary(List<BoardRow> rows) {
        int notStarted = 0;
        int active = 0;
        int offline = 0;
        int locked = 0;
        int finished = 0;
        int flagged = 0;
        for (BoardRow r : rows) {
            switch (r.status()) {
                case ProctorViews.NOT_STARTED -> notStarted++;
                case ProctorViews.LOCKED -> locked++;
                case ProctorViews.FINISHED -> finished++;
                default -> active++;
            }
            if (!r.online() && (ProctorViews.ACTIVE.equals(r.status()) || ProctorViews.LOCKED.equals(r.status()))) {
                offline++;
            }
            if (r.violations() > 0) {
                flagged++;
            }
        }
        return new BoardSummary(rows.size(), notStarted, active, offline, locked, finished, flagged);
    }

    private void notifyTeacher(Assessment a, ProctorSession s, Instant now) {
        Course c = courses.findById(a.getCourseId()).orElse(null);
        if (c == null || c.getTeacher() == null) {
            return;
        }
        RowChanged event = new RowChanged(c.getPublicId(), a.getPublicId(), row(a, s, now));
        relay.sendAfterCommit(List.of(c.getTeacher().getId()), RealtimeEvent.PROCTOR_UPDATED, event);
    }

    private void notifyStudent(Assessment a, ProctorSession s) {
        relay.sendAfterCommit(List.of(s.getUserId()), RealtimeEvent.PROCTOR_STATE,
                new StateChanged(a.getPublicId()));
    }
}
