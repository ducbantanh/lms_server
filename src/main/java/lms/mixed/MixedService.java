package lms.mixed;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.code.CodeService;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
import lms.course.CourseContentService;
import lms.course.CourseFile;
import lms.course.CourseFileRepository;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.CourseViews;
import lms.course.CourseViews.AssessmentView;
import lms.course.EnrollmentRepository;
import lms.course.FileStorage;
import lms.course.UploadChecker;
import lms.course.UploadChecker.Checked;
import lms.mixed.MixedViews.CodePartView;
import lms.mixed.MixedViews.CreateMixedRequest;
import lms.mixed.MixedViews.FilePartView;
import lms.mixed.MixedViews.MixedState;
import lms.mixed.MixedViews.QuizPartView;
import lms.notification.NotificationService;
import lms.question.BankQuestion;
import lms.quiz.QuizQuestionRepository;
import lms.quiz.QuizService;
import lms.quiz.QuizViews;
import lms.submission.Submission;
import lms.submission.SubmissionFileRepository;
import lms.submission.SubmissionRepository;

/**
 * Bài tổng hợp (Assessment.MODE_MIXED): một bài gồm từ hai phần trở lên trong trắc nghiệm, lập trình, nộp file.
 * Giảng viên tạo bài một lần (câu hỏi chép từ ngân hàng, câu lập trình và bộ test, đề PDF của phần nộp file) và đặt
 * điểm tối đa từng phần. Sinh viên bấm "Bắt đầu" một lần, làm các phần qua API sẵn có của từng phần
 * (/quiz/answers, /code/problems, /files), bấm nộp một lần cho cả bài; hết giờ thì QuizSweeper chốt.
 */
@Service
public class MixedService {

    private final AssessmentRepository assessments;
    private final CourseRepository courses;
    private final CourseService courseService;
    private final EnrollmentRepository enrollments;
    private final SubmissionRepository submissions;
    private final SubmissionFileRepository submissionFiles;
    private final CourseFileRepository courseFiles;
    private final FileStorage storage;
    private final UploadChecker uploads;
    private final QuizService quizService;
    private final CodeService codeService;
    private final QuizQuestionRepository questions;
    private final MixedScorer scorer;
    private final MixedLifecycle lifecycle;
    private final NotificationService notifications;
    private final AuditService audit;

    public MixedService(AssessmentRepository assessments, CourseRepository courses, CourseService courseService,
            EnrollmentRepository enrollments, SubmissionRepository submissions,
            SubmissionFileRepository submissionFiles, CourseFileRepository courseFiles, FileStorage storage,
            UploadChecker uploads, QuizService quizService, CodeService codeService, QuizQuestionRepository questions,
            MixedScorer scorer, MixedLifecycle lifecycle, NotificationService notifications, AuditService audit) {
        this.assessments = assessments;
        this.courses = courses;
        this.courseService = courseService;
        this.enrollments = enrollments;
        this.submissions = submissions;
        this.submissionFiles = submissionFiles;
        this.courseFiles = courseFiles;
        this.storage = storage;
        this.uploads = uploads;
        this.quizService = quizService;
        this.codeService = codeService;
        this.questions = questions;
        this.scorer = scorer;
        this.lifecycle = lifecycle;
        this.notifications = notifications;
        this.audit = audit;
    }

    // ================================================================ giảng viên

    /**
     * Tạo bài. parts: các part multipart; "paper" là đề PDF của phần nộp file (không bắt buộc), còn lại là file ZIP bộ
     * test của phần lập trình (ProblemRequest.archive là tên part).
     */
    @Transactional
    public AssessmentView create(String courseId, CreateMixedRequest req, Map<String, MultipartFile> parts,
            AuthPrincipal me) {
        Course c = courseService.requireTeaching(courseId, me);
        if (req.opensAt() != null && req.closesAt() != null && !req.closesAt().isAfter(req.opensAt())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "closesAt must be after opensAt");
        }
        CourseContentService.requireScoreRelease(req.scoreRelease(), req.closesAt());
        BigDecimal quizWeight = req.quiz() == null ? null : req.quiz().weight();
        BigDecimal codeWeight = req.code() == null ? null : req.code().weight();
        BigDecimal fileWeight = req.file() == null ? null : req.file().weight();
        MixedGrader.requireWeights(quizWeight, codeWeight, fileWeight);
        if (req.quiz() != null && req.type() == Assessment.TYPE_PRACTICE) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Practice assessments have no multiple choice part");
        }
        MultipartFile paperUpload = parts.get("paper");
        if (paperUpload != null && !paperUpload.isEmpty() && req.file() == null) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "A paper needs a file part");
        }
        // Kiểm tra câu hỏi trước khi lưu gì (thiếu câu thì NOT_FOUND)
        Map<String, BankQuestion> found = req.quiz() == null ? Map.of()
                : quizService.findOwned(req.quiz().questionIds(), me.userId());

        String key = null;
        try {
            CourseFile paper = null;
            if (paperUpload != null && !paperUpload.isEmpty()) {
                Checked file = uploads.check(paperUpload, UploadChecker.MATERIAL);
                if (!CourseContentService.PDF.equals(file.contentType())) {
                    throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE, "Paper must be a PDF");
                }
                key = storage.store(file.data());
                paper = courseFiles.save(CourseFile.paper(c.getId(), file.name(), file.data().length, key));
            }
            Assessment a = assessments.save(Assessment.mixed(c.getId(), req.type(), req.title().strip(),
                    CourseService.blankToNull(req.description()), paper == null ? null : paper.getId(),
                    req.opensAt(), req.closesAt(), req.timeLimitMinutes(), req.shuffle(), req.revealMode(),
                    req.scoreRelease(), quizWeight, codeWeight, fileWeight));
            int questionCount = req.quiz() == null ? 0 : quizService.copyQuestions(a, req.quiz().questionIds(), found);
            CodeService.Applied code = req.code() == null ? null
                    : codeService.setupProblems(a, req.code().code(), parts, me.userId());
            notifications.assessmentCreated(c, a, req.notifyStudents(), me.userId());
            List<String> summary = new ArrayList<>();
            if (req.quiz() != null) {
                summary.add("trắc nghiệm " + questionCount + " câu = " + quizWeight.toPlainString() + "đ");
            }
            if (code != null) {
                summary.add("lập trình " + code.problems() + " câu = " + codeWeight.toPlainString() + "đ");
            }
            if (req.file() != null) {
                summary.add("nộp file = " + fileWeight.toPlainString() + "đ");
            }
            audit.record(me.userId(), AuditService.ASSESSMENT_CREATED, c.displayCode(),
                    a.getTitle() + " (tổng hợp: " + String.join(", ", summary) + ")");
            return CourseViews.assessmentView(a, paper, questionCount, Instant.now());
        } catch (RuntimeException e) {
            if (key != null) {
                storage.deleteQuietly(key);
            }
            throw e;
        }
    }

    // ================================================================ sinh viên

    @Transactional
    public MixedState state(String assessmentId, AuthPrincipal me) {
        Target t = requireEnrolled(assessmentId, me);
        Instant now = Instant.now();
        Submission s = submissions.findByAssessmentIdAndUserId(t.assessment().getId(), me.userId()).orElse(null);
        if (s != null) {
            lifecycle.finalizeIfDue(t.assessment(), s, now);
        }
        return state(t.assessment(), s, now);
    }

    /** Bắt đầu làm bài: đồng hồ tính chung cho mọi phần (gọi lại khi đã bắt đầu thì chỉ trả về trạng thái). */
    @Transactional
    public MixedState start(String assessmentId, AuthPrincipal me) {
        Target t = requireEnrolled(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = nowMillis();
        if (!a.isOpened(now)) {
            throw new AppException(ErrorCode.ASSESSMENT_NOT_OPEN);
        }
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId()).orElse(null);
        if (s != null && s.getStartedAt() != null) {
            lifecycle.finalizeIfDue(a, s, now);
            return state(a, s, now);
        }
        if (a.isClosed(now) || (s != null && s.isSubmitted())) {
            throw new AppException(ErrorCode.SUBMISSION_LOCKED);
        }
        if (s == null) {
            s = submissions.save(new Submission(a.getId(), me.userId()));
        }
        s.start(now);
        s.touch();
        audit.record(me.userId(), AuditService.QUIZ_STARTED, t.course().displayCode(), a.getTitle() + " (tổng hợp)");
        return state(a, s, now);
    }

    /** Nộp cả bài: mọi phần cùng khóa, chấm phần tự động; đã chốt rồi (tab khác, hết giờ) thì trả về như cũ. */
    @Transactional
    public MixedState submit(String assessmentId, AuthPrincipal me, String ip) {
        Target t = requireEnrolled(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = Instant.now();
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .filter(x -> x.getStartedAt() != null)
                .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST, "Assessment not started"));
        lifecycle.finalizeIfDue(a, s, now);
        if (!s.isSubmitted()) {
            lifecycle.finalize(a, s, nowMillis(), "submitted");
            audit.record(me.userId(), AuditService.SUBMISSION_SUBMITTED, t.course().displayCode(),
                    a.getTitle() + " (tổng hợp, điểm máy chấm " + s.getAutoScore().toPlainString() + ")", ip);
        }
        return state(a, s, now);
    }

    // ---------------------------------------------------------------- nội bộ

    private record Target(Assessment assessment, Course course) {
    }

    private static Instant nowMillis() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    private Target requireEnrolled(String assessmentId, AuthPrincipal me) {
        Assessment a = assessments.findByPublicId(assessmentId).filter(Assessment::isMixed)
                .filter(x -> enrollments.existsByCourseIdAndUserId(x.getCourseId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Assessment"));
        Course c = courses.findById(a.getCourseId()).orElseThrow(() -> AppException.notFound("Assessment"));
        return new Target(a, c);
    }

    private MixedState state(Assessment a, Submission s, Instant now) {
        boolean started = s != null && s.getStartedAt() != null;
        boolean finished = started && s.isSubmitted();
        String phase = !started ? QuizViews.NOT_STARTED : finished ? QuizViews.FINISHED : QuizViews.IN_PROGRESS;
        boolean visible = finished && a.isScoreVisible(now);
        MixedScorer.Parts parts = visible ? scorer.parts(a, s) : null;

        QuizPartView quiz = a.getQuizWeight() == null ? null
                : new QuizPartView(a.getQuizWeight(), (int) questions.countByAssessmentId(a.getId()),
                        parts == null ? null : parts.quiz().correct(), parts == null ? null : parts.quiz().points());
        CodePartView code = a.getCodeWeight() == null ? null
                : new CodePartView(a.getCodeWeight(), (int) assessments.countCodeProblems(a.getId()),
                        parts == null ? null : parts.code().accepted(), parts == null ? null : parts.code().points(),
                        parts != null && parts.code().pending());
        FilePartView file = a.getFileWeight() == null ? null
                : new FilePartView(a.getFileWeight(),
                        s == null ? 0 : (int) submissionFiles.countBySubmissionId(s.getId()),
                        visible ? s.getManualScore() : null);
        return new MixedState(phase, a.getTimeLimitMinutes(), started ? s.getStartedAt() : null,
                started ? a.deadlineFor(s.getStartedAt()) : null, finished ? s.getSubmittedAt() : null, quiz, code,
                file, visible ? s.getScore() : null, visible ? s.getFeedback() : null, finished && !visible, now);
    }
}
