package lms.code;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.persistence.EntityManager;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.code.CodeViews.ArchiveCheck;
import lms.code.CodeViews.ArchiveTest;
import lms.code.CodeViews.AttemptView;
import lms.code.CodeViews.Best;
import lms.code.CodeViews.CodeRequest;
import lms.code.CodeViews.CodeResults;
import lms.code.CodeViews.CodeState;
import lms.code.CodeViews.CreateCodeRequest;
import lms.code.CodeViews.LanguageView;
import lms.code.CodeViews.ProblemDetail;
import lms.code.CodeViews.ProblemRef;
import lms.code.CodeViews.ProblemRequest;
import lms.code.CodeViews.ProblemSummary;
import lms.code.CodeViews.ResultCell;
import lms.code.CodeViews.ResultRow;
import lms.code.CodeViews.ResultView;
import lms.code.CodeViews.RunRequest;
import lms.code.CodeViews.SampleView;
import lms.code.CodeViews.StudentCode;
import lms.code.CodeViews.TeacherCode;
import lms.code.CodeViews.TeacherProblem;
import lms.code.CodeViews.TestRequest;
import lms.code.CodeViews.TestView;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseContentService;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.CourseViews;
import lms.course.CourseViews.AssessmentView;
import lms.course.EnrollmentRepository;
import lms.notification.NotificationService;
import lms.report.ReportService;
import lms.submission.Submission;
import lms.submission.SubmissionRepository;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Bài lập trình (Assessment.MODE_CODE) gồm nhiều câu, không chấm điểm. Giảng viên phụ trách soạn từng câu (đề,
 * giới hạn, bộ test: test mẫu sinh viên thấy và test ẩn, soạn tay hoặc file ZIP). Sinh viên chạy thử với test mẫu
 * hoặc nộp bài (mọi test) nhiều lần trong giờ làm bài; mỗi lần vào hàng đợi, CodeJudgeWorker chấm trong sandbox
 * rồi gọi complete(). Kết quả một câu là số test đúng / tổng số test; câu "đúng" (AC) khi có lần nộp đúng hết.
 * Sinh viên chỉ thấy kết quả chung của lần nộp (không thấy từng test); giảng viên xem được từng test.
 * Kết quả các lần nộp công bố theo assessment.scoreRelease (ngay, sau giờ đóng, không công bố).
 */
@Service
public class CodeService {

    /** Tổng dung lượng input + output bộ test của một câu (byte UTF-8). */
    static final long MAX_TEST_BYTES = 64L << 20;
    /** Mỗi file input/output trong ZIP bộ test. */
    static final long MAX_ARCHIVE_FILE_BYTES = 8L << 20;
    static final TestArchive.Limits ARCHIVE_LIMITS = new TestArchive.Limits(CodeViews.MAX_TESTS,
            MAX_ARCHIVE_FILE_BYTES, MAX_TEST_BYTES);
    /** Số lần chạy thử giữ lại cho mỗi sinh viên ở mỗi câu (cũ hơn thì xóa). */
    static final int KEEP_RUNS = 20;

    private final AssessmentRepository assessments;
    private final CourseRepository courses;
    private final CourseService courseService;
    private final EnrollmentRepository enrollments;
    private final SubmissionRepository submissions;
    private final UserRepository users;
    private final CodeSettingsRepository settings;
    private final CodeProblemRepository problems;
    private final CodeTestRepository tests;
    private final CodeAttemptRepository attempts;
    private final NotificationService notifications;
    private final AuditService audit;
    private final EntityManager em;

    public CodeService(AssessmentRepository assessments, CourseRepository courses, CourseService courseService,
            EnrollmentRepository enrollments, SubmissionRepository submissions, UserRepository users,
            CodeSettingsRepository settings, CodeProblemRepository problems, CodeTestRepository tests,
            CodeAttemptRepository attempts, NotificationService notifications, AuditService audit, EntityManager em) {
        this.assessments = assessments;
        this.courses = courses;
        this.courseService = courseService;
        this.enrollments = enrollments;
        this.submissions = submissions;
        this.users = users;
        this.settings = settings;
        this.problems = problems;
        this.tests = tests;
        this.attempts = attempts;
        this.notifications = notifications;
        this.audit = audit;
        this.em = em;
    }

    // ================================================================ giảng viên

    /** files: các part multipart (file ZIP bộ test, ProblemRequest.archive là tên part); rỗng khi gửi JSON. */
    @Transactional
    public AssessmentView create(String courseId, CreateCodeRequest req, Map<String, MultipartFile> files,
            AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        if (req.opensAt() != null && req.closesAt() != null && !req.closesAt().isAfter(req.opensAt())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "closesAt must be after opensAt");
        }
        CourseContentService.requireScoreRelease(req.scoreRelease(), req.closesAt());
        List<CodeLanguage> languages = languages(req.code());
        List<List<TestArchive.Test>> archives = archives(req.code(), files);
        Assessment a = assessments.save(Assessment.code(c.getId(), req.type(), req.title().strip(),
                CourseService.blankToNull(req.description()), req.opensAt(), req.closesAt(), req.scoreRelease()));
        CodeSettings s = new CodeSettings(a.getId());
        s.setLanguages(languages);
        settings.save(s);
        Applied applied = applyProblems(a, req.code(), archives);
        notifications.assessmentCreated(c, a, req.notifyStudents(), me.userId());
        audit.record(me.userId(), AuditService.ASSESSMENT_CREATED, c.displayCode(),
                a.getTitle() + " (lập trình, " + applied.problems() + " câu, " + applied.tests() + " test)");
        return CourseViews.assessmentView(a, null, applied.problems(), Instant.now());
    }

    /** Ngôn ngữ, các câu và bộ test đầy đủ; admin xem được. */
    @Transactional(readOnly = true)
    public TeacherCode teacherCode(String courseId, String assessmentId, AuthPrincipal me) {
        Managed m = requireManagedCode(courseId, assessmentId, me);
        return teacherCode(m, me, Instant.now());
    }

    /**
     * Sửa ngôn ngữ, các câu và bộ test (giữ câu/test theo id, thêm mới, thêm test từ ZIP; câu không còn trong danh
     * sách bị xóa cùng các lần nộp). Câu có bộ test hoặc giới hạn thay đổi thì mọi lần nộp của câu được chấm lại
     * (kết quả chạy thử cũ bị bỏ). Bài kiểm tra đã kết thúc là hồ sơ thi: ASSESSMENT_CLOSED.
     */
    @Transactional
    public TeacherCode updateCode(String courseId, String assessmentId, CodeRequest req,
            Map<String, MultipartFile> files, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        Assessment a = requireCode(c, assessmentId);
        Instant now = Instant.now();
        if (a.isLocked(now)) {
            throw new AppException(ErrorCode.ASSESSMENT_CLOSED);
        }
        List<CodeLanguage> languages = languages(req);
        List<List<TestArchive.Test>> archives = archives(req, files);
        CodeSettings s = settings.findById(a.getId()).orElseThrow(() -> AppException.notFound("Assessment"));
        s.setLanguages(languages);
        Applied applied = applyProblems(a, req, archives);
        audit.record(me.userId(), AuditService.ASSESSMENT_UPDATED, c.displayCode(), a.getTitle() + " (lập trình, "
                + applied.problems() + " câu, " + applied.tests() + " test, chấm lại " + applied.requeued()
                + " lần nộp)");
        return teacherCode(new Managed(c, a, s), me, now);
    }

    /** Chấm lại mọi lần nộp (vd. sau khi sửa lỗi máy chấm); kể cả bài kiểm tra đã kết thúc. */
    @Transactional
    public TeacherCode rejudge(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        Assessment a = requireCode(c, assessmentId);
        CodeSettings s = settings.findById(a.getId()).orElseThrow(() -> AppException.notFound("Assessment"));
        List<CodeAttempt> list = attempts.findByAssessmentIdAndKind(a.getId(), CodeAttempt.KIND_SUBMIT);
        list.forEach(CodeAttempt::requeue);
        audit.record(me.userId(), AuditService.CODE_REJUDGED, c.displayCode(),
                a.getTitle() + " (" + list.size() + " lần nộp)");
        return teacherCode(new Managed(c, a, s), me, Instant.now());
    }

    /** Đọc thử file ZIP bộ test để giảng viên xem trước khi lưu; không lưu gì. */
    public ArchiveCheck checkArchive(String courseId, MultipartFile archive, AuthPrincipal me) {
        courseService.requireTeaching(courseId, me);
        TestArchive.Result r = TestArchive.read(bytes(archive), ARCHIVE_LIMITS);
        return new ArchiveCheck(r.tests().stream().map(t -> new ArchiveTest(t.name(), utf8(t.input()),
                utf8(t.expected()))).toList(), r.problems(), r.ignored());
    }

    /** Bộ test một câu để tải về dạng ZIP: tên file và test (nội dung đọc từng test bằng testContent). */
    public record TestExport(String fileName, List<CodeTestInfo> tests) {
    }

    @Transactional(readOnly = true)
    public TestExport exportTests(String courseId, String assessmentId, String problemId, AuthPrincipal me) {
        Managed m = requireManagedCode(courseId, assessmentId, me);
        CodeProblem p = requireProblem(m.assessment(), problemId);
        return new TestExport(ReportService.fileSafe("test - " + m.course().displayCode() + " - "
                + m.assessment().getTitle() + " - Câu " + (p.getPosition() + 1)) + ".zip", tests.findInfos(p.getId()));
    }

    /** Nội dung đầy đủ một test; null nếu test đã bị xóa. */
    public CodeJudge.Content testContent(long testId) {
        List<Object[]> rows = tests.content(testId);
        return rows.isEmpty() ? null : new CodeJudge.Content((String) rows.get(0)[0], (String) rows.get(0)[1]);
    }

    /** Bảng sinh viên × câu (giảng viên luôn thấy kết quả, kể cả khi chưa công bố cho sinh viên). */
    @Transactional(readOnly = true)
    public CodeResults results(String courseId, String assessmentId, AuthPrincipal me) {
        Managed m = requireManagedCode(courseId, assessmentId, me);
        List<CodeProblem> ps = problems.findByAssessmentIdOrderByPosition(m.assessment().getId());
        Map<Long, Map<Long, List<CodeAttemptInfo>>> byUser = attempts.submitInfos(m.assessment().getId()).stream()
                .sorted(Comparator.comparing(CodeAttemptInfo::id).reversed())
                .collect(Collectors.groupingBy(CodeAttemptInfo::userId,
                        Collectors.groupingBy(CodeAttemptInfo::problemId)));
        List<ResultRow> rows = enrollments.findWithUsers(m.course().getId()).stream().map(row -> {
            User u = (User) row[1];
            Map<Long, List<CodeAttemptInfo>> mine = byUser.getOrDefault(u.getId(), Map.of());
            List<ResultCell> cells = ps.stream().map(p -> {
                Outcome o = outcome(mine.getOrDefault(p.getId(), List.of()), true);
                return new ResultCell(p.getPublicId(), o.status(), o.best(), o.attempts());
            }).toList();
            return new ResultRow(u.getPublicId(), u.getUsername(), u.getFullName(), cells);
        }).toList();
        return new CodeResults(ps.stream().map(p -> new ProblemRef(p.getPublicId(), p.getTitle())).toList(), rows);
    }

    /** Các lần chạy/nộp của một sinh viên đang ghi danh ở mọi câu (không kèm mã nguồn). */
    @Transactional(readOnly = true)
    public StudentCode studentCode(String courseId, String assessmentId, String studentId, AuthPrincipal me) {
        Managed m = requireManagedCode(courseId, assessmentId, me);
        User u = users.findByPublicId(studentId)
                .filter(x -> enrollments.existsByCourseIdAndUserId(m.course().getId(), x.getId()))
                .orElseThrow(() -> AppException.notFound("Student"));
        Map<Long, String> ids = problemIds(m.assessment());
        List<AttemptView> list = attempts.infosOfUser(m.assessment().getId(), u.getId()).stream()
                .map(x -> summary(x, ids, true)).toList();
        return new StudentCode(u.getPublicId(), u.getUsername(), u.getFullName(), list);
    }

    /** Chi tiết một lần chạy/nộp: mã nguồn, lỗi biên dịch, kết quả mọi test kèm output đã giữ lại. */
    @Transactional(readOnly = true)
    public AttemptView teacherAttempt(String courseId, String assessmentId, String attemptId, AuthPrincipal me) {
        Managed m = requireManagedCode(courseId, assessmentId, me);
        CodeAttempt x = attempts.findByPublicIdAndAssessmentId(attemptId, m.assessment().getId())
                .orElseThrow(() -> AppException.notFound("Attempt"));
        return detail(x, problemIds(m.assessment()), testsFor(x), true);
    }

    // ================================================================ sinh viên

    /** Danh sách câu kèm tình trạng của mình ở từng câu. */
    @Transactional(readOnly = true)
    public CodeState state(String assessmentId, AuthPrincipal me) {
        Enrolled t = requireEnrolledCode(assessmentId, me);
        Instant now = Instant.now();
        boolean visible = t.assessment().isScoreVisible(now);
        List<CodeProblem> ps = problems.findByAssessmentIdOrderByPosition(t.assessment().getId());
        Map<Long, Long> counts = testCounts(ps);
        Map<Long, List<CodeAttemptInfo>> mine = attempts.infosOfUser(t.assessment().getId(), me.userId()).stream()
                .collect(Collectors.groupingBy(CodeAttemptInfo::problemId));
        List<ProblemSummary> list = ps.stream().map(p -> problemSummary(p, counts.getOrDefault(p.getId(), 0L),
                mine.getOrDefault(p.getId(), List.of()), visible)).toList();
        return new CodeState(languageViews(t.settings()), list, !visible, isOpen(t.assessment(), now), now);
    }

    /** Một câu: đề, test mẫu, các lần chạy/nộp của mình ở câu này. */
    @Transactional(readOnly = true)
    public ProblemDetail problem(String assessmentId, String problemId, AuthPrincipal me) {
        Enrolled t = requireEnrolledCode(assessmentId, me);
        CodeProblem p = requireProblem(t.assessment(), problemId);
        Instant now = Instant.now();
        boolean visible = t.assessment().isScoreVisible(now);
        List<CodeTestInfo> all = tests.findInfos(p.getId());
        List<CodeAttemptInfo> mine = attempts.infosOfProblem(p.getId(), me.userId());
        Map<Long, String> ids = Map.of(p.getId(), p.getPublicId());
        return new ProblemDetail(problemSummary(p, all.size(), mine, visible), p.getStatement(),
                all.stream().filter(CodeTestInfo::sample).map(x -> new SampleView(x.input(), x.expected())).toList(),
                mine.stream().map(x -> summary(x, ids, visible)).toList(), languageViews(t.settings()), !visible,
                isOpen(t.assessment(), now), now);
    }

    /** Chạy thử với các test mẫu: không tính là một lần nộp. */
    @Transactional
    public AttemptView run(String assessmentId, String problemId, RunRequest req, AuthPrincipal me) {
        Enrolled t = requireEnrolledCode(assessmentId, me);
        CodeProblem p = requireProblem(t.assessment(), problemId);
        CodeAttempt x = enqueue(t, p, req, CodeAttempt.KIND_RUN, me);
        // Chỉ giữ vài lần chạy thử gần nhất
        List<Long> old = attempts.infosOfProblem(p.getId(), me.userId()).stream()
                .filter(at -> !at.isSubmit() && at.isDone()).skip(KEEP_RUNS).map(CodeAttemptInfo::id).toList();
        if (!old.isEmpty()) {
            attempts.deleteByIds(old);
        }
        return summary(x, p, true);
    }

    /** Nộp bài một câu: chấm với mọi test; lần nộp đầu của bài tạo bài nộp (trạng thái "đã nộp"). */
    @Transactional
    public AttemptView submit(String assessmentId, String problemId, RunRequest req, AuthPrincipal me, String ip) {
        Enrolled t = requireEnrolledCode(assessmentId, me);
        Assessment a = t.assessment();
        CodeProblem p = requireProblem(a, problemId);
        CodeAttempt x = enqueue(t, p, req, CodeAttempt.KIND_SUBMIT, me);
        Instant now = x.getCreatedAt();
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .orElseGet(() -> submissions.save(new Submission(a.getId(), me.userId())));
        s.submit(now);
        s.touch();
        audit.record(me.userId(), AuditService.SUBMISSION_SUBMITTED, t.course().displayCode(),
                a.getTitle() + " · Câu " + (p.getPosition() + 1) + " (" + x.getLanguage().label() + ", "
                        + req.source().length() + " ký tự)",
                ip);
        return summary(x, p, a.isScoreVisible(now));
    }

    /**
     * Một lần chạy/nộp của mình kèm mã nguồn. Chạy thử: kết quả từng test mẫu. Nộp bài: chỉ kết quả chung (và lỗi
     * biên dịch), không có kết quả từng test; kết quả chưa công bố thì chỉ có trạng thái.
     */
    @Transactional(readOnly = true)
    public AttemptView attempt(String assessmentId, String attemptId, AuthPrincipal me) {
        Enrolled t = requireEnrolledCode(assessmentId, me);
        Assessment a = t.assessment();
        CodeAttempt x = attempts.findByPublicIdAndAssessmentIdAndUserId(attemptId, a.getId(), me.userId())
                .orElseThrow(() -> AppException.notFound("Attempt"));
        Map<Long, String> ids = problemIds(a);
        if (!x.isSubmit()) {
            return detail(x, ids, testsFor(x), false);
        }
        boolean visible = a.isScoreVisible(Instant.now());
        AttemptView v = summary(info(x), ids, visible);
        return withSource(v, x.getSource(),
                visible && CodeJudge.CE.equals(x.getVerdict()) ? x.getCompileOutput() : null, null);
    }

    // ================================================================ CodeJudgeWorker

    /** Bài đã nhận để chấm: token để complete() biết kết quả còn dùng được không. */
    public record Claimed(long attemptId, String token, CodeJudge.Task task) {
    }

    @Transactional(readOnly = true)
    public List<Long> queued(int limit) {
        return attempts.findQueuedIds(Limit.of(limit));
    }

    /** Nhận một bài đang chờ (đặt RUNNING); empty nếu bài đã bị worker khác nhận hoặc đã xóa. */
    @Transactional
    public Optional<Claimed> claim(long attemptId) {
        CodeAttempt x = attempts.lockById(attemptId).orElse(null);
        if (x == null || x.getStatus() != CodeAttempt.QUEUED) {
            return Optional.empty();
        }
        CodeProblem p = problems.findById(x.getProblemId()).orElse(null);
        if (p == null) {
            return Optional.empty();
        }
        String token = x.claim();
        // Nội dung từng test đọc khi tới lượt chạy (repository tự mở giao dịch đọc)
        List<CodeJudge.TestCase> cases = testsFor(x).stream()
                .map(t -> new CodeJudge.TestCase(t.sample(), () -> testContent(t.id()))).toList();
        return Optional.of(new Claimed(x.getId(), token, new CodeJudge.Task(x.getLanguage(), x.getSource(), cases,
                p.getTimeLimitMs(), p.getMemoryLimitMb())));
    }

    /** Lưu kết quả; bài đã bị đưa về hàng đợi (chấm lại) hoặc bị xóa trong lúc chạy thì bỏ qua. */
    @Transactional
    public void complete(long attemptId, String token, CodeJudge.Result result) {
        CodeAttempt x = attempts.lockById(attemptId).orElse(null);
        if (x == null || x.getStatus() != CodeAttempt.RUNNING || !token.equals(x.getClaimToken())) {
            return;
        }
        x.finish(result, Instant.now());
    }

    @Transactional
    public int requeueInterrupted() {
        return attempts.requeueRunning();
    }

    // ---------------------------------------------------------------- nội bộ: quyền

    private record Managed(Course course, Assessment assessment, CodeSettings settings) {
    }

    private record Enrolled(Course course, Assessment assessment, CodeSettings settings) {
    }

    private Assessment requireCode(Course c, String assessmentId) {
        return assessments.findByPublicIdAndCourseId(assessmentId, c.getId()).filter(Assessment::isCode)
                .orElseThrow(() -> AppException.notFound("Assessment"));
    }

    private Managed requireManagedCode(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courseService.requireManaged(courseId, me);
        Assessment a = requireCode(c, assessmentId);
        CodeSettings s = settings.findById(a.getId()).orElseThrow(() -> AppException.notFound("Assessment"));
        return new Managed(c, a, s);
    }

    /** Bài chưa mở thì sinh viên chưa được xem đề: ASSESSMENT_NOT_OPEN. */
    private Enrolled requireEnrolledCode(String assessmentId, AuthPrincipal me) {
        Assessment a = assessments.findByPublicId(assessmentId).filter(Assessment::isCode)
                .filter(x -> enrollments.existsByCourseIdAndUserId(x.getCourseId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Assessment"));
        Course c = courses.findById(a.getCourseId()).orElseThrow(() -> AppException.notFound("Assessment"));
        CodeSettings s = settings.findById(a.getId()).orElseThrow(() -> AppException.notFound("Assessment"));
        if (!a.isOpened(Instant.now())) {
            throw new AppException(ErrorCode.ASSESSMENT_NOT_OPEN);
        }
        return new Enrolled(c, a, s);
    }

    private CodeProblem requireProblem(Assessment a, String problemId) {
        return problems.findByPublicIdAndAssessmentId(problemId, a.getId())
                .orElseThrow(() -> AppException.notFound("Problem"));
    }

    private static boolean isOpen(Assessment a, Instant now) {
        return a.isOpened(now) && !a.isClosed(now);
    }

    private CodeAttempt enqueue(Enrolled t, CodeProblem p, RunRequest req, int kind, AuthPrincipal me) {
        Assessment a = t.assessment();
        if (a.isClosed(Instant.now())) {
            throw new AppException(ErrorCode.SUBMISSION_LOCKED);
        }
        CodeLanguage lang = CodeLanguage.of(req.language()).filter(t.settings().getLanguages()::contains)
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_FAILED, "Language not allowed"));
        if (attempts.hasPending(p.getId(), me.userId())) {
            throw new AppException(ErrorCode.JUDGE_BUSY);
        }
        if (kind == CodeAttempt.KIND_RUN && tests.findInfos(p.getId()).stream().noneMatch(CodeTestInfo::sample)) {
            throw new AppException(ErrorCode.BAD_REQUEST, "No sample tests");
        }
        if (kind == CodeAttempt.KIND_SUBMIT && p.getMaxAttempts() != null
                && attempts.countCountedSubmits(p.getId(), me.userId()) >= p.getMaxAttempts()) {
            throw new AppException(ErrorCode.ATTEMPT_LIMIT);
        }
        return attempts.save(new CodeAttempt(a.getId(), p.getId(), me.userId(), kind, lang, req.source()));
    }

    // ---------------------------------------------------------------- nội bộ: soạn bài

    private static List<CodeLanguage> languages(CodeRequest req) {
        List<CodeLanguage> list = new ArrayList<>();
        for (String code : req.languages()) {
            CodeLanguage l = CodeLanguage.of(code)
                    .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_FAILED, "Unknown language " + code));
            if (!list.contains(l)) {
                list.add(l);
            }
        }
        return list;
    }

    /** Đọc trước mọi file ZIP (lỗi thì chưa ghi gì); phần tử i là test trong ZIP của câu i (rỗng nếu không có). */
    private static List<List<TestArchive.Test>> archives(CodeRequest req, Map<String, MultipartFile> files) {
        List<List<TestArchive.Test>> out = new ArrayList<>();
        for (ProblemRequest p : req.problems()) {
            if (p.archive() == null) {
                out.add(List.of());
                continue;
            }
            MultipartFile file = files.get(p.archive());
            if (file == null || file.isEmpty()) {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "Missing archive part " + p.archive());
            }
            TestArchive.Result r = TestArchive.read(bytes(file), ARCHIVE_LIMITS);
            if (!r.ok()) {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid test archive: " + r.problems());
            }
            out.add(r.tests());
        }
        return out;
    }

    private static byte[] bytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Cannot read upload");
        }
    }

    private static long utf8(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length;
    }

    private record Applied(int problems, int tests, int requeued) {
    }

    /**
     * Ghi các câu theo thứ tự: giữ câu có id, thêm câu mới, xóa câu không còn (cùng lần nộp). Câu đang có mà bộ
     * test hoặc giới hạn đổi thì đưa mọi lần nộp của câu về hàng đợi.
     */
    private Applied applyProblems(Assessment a, CodeRequest req, List<List<TestArchive.Test>> archives) {
        Map<String, CodeProblem> existing = problems.findByAssessmentIdOrderByPosition(a.getId()).stream()
                .collect(Collectors.toMap(CodeProblem::getPublicId, Function.identity()));
        Set<Long> kept = new HashSet<>();
        int testCount = 0;
        int requeued = 0;
        for (int i = 0; i < req.problems().size(); i++) {
            ProblemRequest r = req.problems().get(i);
            CodeProblem p = r.id() == null ? null : existing.get(r.id());
            if (r.id() != null && p == null) {
                throw AppException.notFound("Problem");
            }
            if (p != null && !kept.add(p.getId())) {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "Duplicate problem");
            }
            String before = p == null ? null : signature(p, tests.findInfos(p.getId()));
            if (p == null) {
                p = new CodeProblem(a.getId());
            }
            p.update(i, r.title().strip(), r.statement().strip(), r.timeLimitMs(), r.memoryLimitMb(),
                    r.maxAttempts());
            p = problems.save(p);
            testCount += applyTests(p, r.tests(), archives.get(i));
            if (before != null && !before.equals(signature(p, tests.findInfos(p.getId())))) {
                requeued += requeue(p);
            }
        }
        List<CodeProblem> removed = existing.values().stream().filter(p -> !kept.contains(p.getId())).toList();
        // Xóa câu: test, lần chạy/nộp của câu xóa theo (ON DELETE CASCADE)
        problems.deleteAll(removed);
        return new Applied(req.problems().size(), testCount, requeued);
    }

    /** Những gì quyết định kết quả chấm: giới hạn và thứ tự các test (kể cả test mẫu, cho lần chạy thử). */
    private static String signature(CodeProblem p, List<CodeTestInfo> list) {
        return p.getTimeLimitMs() + "/" + p.getMemoryLimitMb() + ":" + list.stream()
                .map(t -> t.publicId() + (t.sample() ? "*" : "")).collect(Collectors.joining(","));
    }

    /** Đưa mọi lần nộp của câu về hàng đợi; kết quả chạy thử cũ bị bỏ. Trả về số lần nộp. */
    private int requeue(CodeProblem p) {
        int n = 0;
        for (CodeAttempt x : attempts.findByProblemId(p.getId())) {
            if (x.isSubmit()) {
                x.requeue();
                n++;
            } else if (x.isDone()) {
                x.getResults().clear();
            } else {
                x.requeue();
            }
        }
        return n;
    }

    /**
     * Ghi bộ test của một câu: giữ test có id (đổi thứ tự, test mẫu), thêm test có nội dung, thêm test trong ZIP ở
     * cuối (test ẩn); test cũ không còn trong danh sách bị xóa. Trả về số test.
     */
    private int applyTests(CodeProblem p, List<TestRequest> list, List<TestArchive.Test> fromArchive) {
        Map<String, CodeTestInfo> existing = tests.findInfos(p.getId()).stream()
                .collect(Collectors.toMap(CodeTestInfo::publicId, Function.identity()));
        int count = list.size() + fromArchive.size();
        if (count == 0 || count > CodeViews.MAX_TESTS) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Test count");
        }
        Set<Long> kept = new HashSet<>();
        long total = 0;
        List<CodeTest> added = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TestRequest t = list.get(i);
            CodeTestInfo old = t.id() == null ? null : existing.get(t.id());
            if (t.id() != null && old == null) {
                throw AppException.notFound("Test");
            }
            long inputSize;
            long expectedSize;
            if (t.input() != null || t.expected() != null) {
                if (t.input() == null || t.expected() == null) {
                    throw new AppException(ErrorCode.VALIDATION_FAILED, "Test needs input and expected");
                }
                CodeTest fresh = new CodeTest(p.getId(), i, CourseService.blankToNull(t.name()) == null ? null
                        : t.name().strip(), t.input(), t.expected(), t.sample());
                added.add(fresh);
                inputSize = fresh.getInputSize();
                expectedSize = fresh.getExpectedSize();
            } else if (old != null) {
                if (!kept.add(old.id())) {
                    throw new AppException(ErrorCode.VALIDATION_FAILED, "Duplicate test");
                }
                tests.updateMeta(old.id(), i, t.sample());
                inputSize = old.inputSize();
                expectedSize = old.expectedSize();
            } else {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "Test needs input and expected");
            }
            // Test mẫu hiện nguyên văn cho sinh viên: phải nhỏ
            if (t.sample() && (inputSize > CodeViews.INLINE_LIMIT || expectedSize > CodeViews.INLINE_LIMIT)) {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "Sample test too large");
            }
            total += inputSize + expectedSize;
        }
        for (int i = 0; i < fromArchive.size(); i++) {
            TestArchive.Test t = fromArchive.get(i);
            CodeTest fresh = new CodeTest(p.getId(), list.size() + i, t.name(), t.input(), t.expected(), false);
            added.add(fresh);
            total += fresh.getInputSize() + fresh.getExpectedSize();
        }
        if (total > MAX_TEST_BYTES) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Test data too large");
        }
        List<Long> removed = existing.values().stream().map(CodeTestInfo::id).filter(id -> !kept.contains(id))
                .toList();
        if (!removed.isEmpty()) {
            tests.deleteByIds(removed);
        }
        tests.saveAll(added);
        // Nội dung test có thể lớn: ghi xuống DB rồi bỏ khỏi bộ nhớ trước khi sang câu sau
        em.flush();
        added.forEach(em::detach);
        return count;
    }

    // ---------------------------------------------------------------- nội bộ: dựng view

    /** Test của một lần chạy, đúng thứ tự kết quả: chạy thử chỉ có test mẫu. */
    private List<CodeTestInfo> testsFor(CodeAttempt x) {
        List<CodeTestInfo> all = tests.findInfos(x.getProblemId());
        return x.isSubmit() ? all : all.stream().filter(CodeTestInfo::sample).toList();
    }

    private Map<Long, Long> testCounts(List<CodeProblem> ps) {
        return ps.isEmpty() ? Map.of()
                : tests.countByProblemIds(ps.stream().map(CodeProblem::getId).toList()).stream()
                        .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
    }

    private Map<Long, String> problemIds(Assessment a) {
        return problems.findByAssessmentIdOrderByPosition(a.getId()).stream()
                .collect(Collectors.toMap(CodeProblem::getId, CodeProblem::getPublicId));
    }

    private TeacherCode teacherCode(Managed m, AuthPrincipal me, Instant now) {
        Assessment a = m.assessment();
        List<CodeProblem> ps = problems.findByAssessmentIdOrderByPosition(a.getId());
        Map<Long, List<CodeTestInfo>> byProblem = ps.isEmpty() ? Map.of()
                : tests.findInfos(ps.stream().map(CodeProblem::getId).toList()).stream()
                        .collect(Collectors.groupingBy(CodeTestInfo::problemId));
        List<TeacherProblem> list = ps.stream().map(p -> new TeacherProblem(p.getPublicId(), p.getTitle(),
                p.getStatement(), p.getTimeLimitMs(), p.getMemoryLimitMb(), p.getMaxAttempts(),
                byProblem.getOrDefault(p.getId(), List.of()).stream()
                        .map(t -> new TestView(t.publicId(), t.name(), t.input(), t.expected(), t.inputTruncated(),
                                t.expectedTruncated(), t.inputSize(), t.expectedSize(), t.sample()))
                        .toList()))
                .toList();
        // requireManaged cho admin xem; chỉ giảng viên phụ trách mới sửa
        boolean editable = !a.isLocked(now) && CourseService.isTeacherOf(m.course(), me);
        return new TeacherCode(m.settings().getLanguages().stream().map(CodeLanguage::code).toList(), list,
                attempts.countPending(a.getId()), editable);
    }

    private static List<LanguageView> languageViews(CodeSettings s) {
        return s.getLanguages().stream().map(l -> new LanguageView(l.code(), l.label(), l.timeFactor())).toList();
    }

    record Outcome(String status, Best best, int attempts) {
    }

    /**
     * Tình trạng một câu từ các lần chạy/nộp (mới nhất trước) của một sinh viên: AC nếu có lần nộp AC, đang chấm nếu
     * còn lần nộp chờ chấm, còn lại là chưa đúng; lần chạy thử và lỗi máy chấm không tính. visible false: chỉ biết
     * đã nộp / đang chấm.
     */
    static Outcome outcome(List<CodeAttemptInfo> list, boolean visible) {
        List<CodeAttemptInfo> counted = list.stream().filter(x -> x.isSubmit() && !x.isSystemError()).toList();
        if (counted.isEmpty()) {
            return new Outcome(CodeViews.NONE, null, 0);
        }
        boolean pending = counted.stream().anyMatch(x -> !x.isDone());
        if (!visible) {
            boolean judged = counted.stream().anyMatch(CodeAttemptInfo::isDone);
            return new Outcome(pending && !judged ? CodeViews.PENDING : CodeViews.SUBMITTED, null, counted.size());
        }
        CodeAttemptInfo best = counted.stream().filter(CodeAttemptInfo::isAccepted).findFirst()
                .orElseGet(() -> counted.stream().filter(CodeAttemptInfo::isDone)
                        .max(Comparator.comparingInt((CodeAttemptInfo x) -> x.passed() == null ? 0 : x.passed())
                                .thenComparing(CodeAttemptInfo::id))
                        .orElse(null));
        String status = best != null && best.isAccepted() ? CodeViews.ACCEPTED
                : pending ? CodeViews.PENDING : CodeViews.REJECTED;
        return new Outcome(status, best == null ? null : new Best(best.verdict(), nz(best.passed()),
                nz(best.total())), counted.size());
    }

    private static int nz(Integer n) {
        return n == null ? 0 : n;
    }

    private static ProblemSummary problemSummary(CodeProblem p, long testCount, List<CodeAttemptInfo> mine,
            boolean visible) {
        Outcome o = outcome(mine, visible);
        return new ProblemSummary(p.getPublicId(), p.getPosition() + 1, p.getTitle(), p.getTimeLimitMs(),
                p.getMemoryLimitMb(), p.getMaxAttempts(), o.attempts(), (int) testCount, o.status(), o.best());
    }

    private static String statusOf(int status) {
        return switch (status) {
            case CodeAttempt.QUEUED -> CodeViews.QUEUED;
            case CodeAttempt.RUNNING -> CodeViews.RUNNING;
            default -> CodeViews.DONE;
        };
    }

    private static CodeAttemptInfo info(CodeAttempt x) {
        return new CodeAttemptInfo(x.getId(), x.getPublicId(), x.getProblemId(), x.getUserId(), x.getKind(),
                x.getLanguage().code(), x.getStatus(), x.getVerdict(), x.getPassed(), x.getTotal(), x.getCreatedAt(),
                x.getJudgedAt());
    }

    private static AttemptView summary(CodeAttempt x, CodeProblem p, boolean showResult) {
        return summary(info(x), Map.of(p.getId(), p.getPublicId()), showResult);
    }

    /** Không kèm mã nguồn, kết quả từng test; showResult false: ẩn kết quả lần nộp (chưa công bố). */
    private static AttemptView summary(CodeAttemptInfo x, Map<Long, String> problemIds, boolean showResult) {
        boolean show = showResult || !x.isSubmit();
        return new AttemptView(x.publicId(), problemIds.get(x.problemId()),
                x.isSubmit() ? CodeViews.SUBMIT : CodeViews.RUN, x.language(), statusOf(x.status()),
                show ? x.verdict() : null, show ? x.passed() : null, show ? x.total() : null, x.createdAt(),
                x.judgedAt(), null, null, null);
    }

    /** teacher: kết quả và output mọi test đã giữ lại; sinh viên (chỉ lần chạy thử) thấy output test mẫu. */
    private static AttemptView detail(CodeAttempt x, Map<Long, String> problemIds, List<CodeTestInfo> testList,
            boolean teacher) {
        List<CodeResult> rs = x.getResults();
        List<ResultView> results = IntStream.range(0, rs.size()).mapToObj(i -> {
            CodeResult r = rs.get(i);
            boolean sample = i < testList.size() && testList.get(i).sample();
            boolean showOutput = teacher || sample;
            return new ResultView(i, sample, r.getVerdict(), r.getTimeMs(), showOutput ? r.getOutput() : null,
                    showOutput ? r.getError() : null);
        }).toList();
        return withSource(summary(info(x), problemIds, true), x.getSource(), x.getCompileOutput(), results);
    }

    private static AttemptView withSource(AttemptView v, String source, String compileOutput,
            List<ResultView> results) {
        return new AttemptView(v.id(), v.problemId(), v.kind(), v.language(), v.status(), v.verdict(), v.passed(),
                v.total(), v.createdAt(), v.judgedAt(), source, compileOutput, results);
    }
}
