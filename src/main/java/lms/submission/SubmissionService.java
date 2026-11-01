package lms.submission;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.course.Course;
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
import lms.mixed.MixedLifecycle;
import lms.proctor.ProctorService;
import lms.quiz.QuizQuestionRepository;
import lms.submission.SubmissionViews.CourseRef;
import lms.submission.SubmissionViews.GradeView;
import lms.submission.SubmissionViews.MyResult;
import lms.submission.SubmissionViews.StudentAssessment;
import lms.submission.SubmissionViews.SubmissionFileView;
import lms.submission.SubmissionViews.SubmissionView;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Sinh viên làm bài: xem bài (đề chỉ hiện khi bài mở), tải file bài làm lên trong lúc bài mở,
 * bấm nộp để khóa bài làm. Hết giờ thì không sửa được nữa; file đã tải được tính là bài nộp.
 * Bài của khóa học chưa ghi danh trả NOT_FOUND để không lộ sự tồn tại.
 */
@Service
public class SubmissionService {

    /** Số file tối đa trong một bài nộp. */
    static final int MAX_FILES = 10;

    /** File để controller trả về: metadata + đường dẫn trên đĩa. */
    public record Download(SubmissionFile file, Path path) {
    }

    private record Target(Assessment assessment, Course course) {
    }

    private final AssessmentRepository assessments;
    private final CourseRepository courses;
    private final EnrollmentRepository enrollments;
    private final CourseFileRepository courseFiles;
    private final SubmissionRepository submissions;
    private final SubmissionFileRepository files;
    private final FileStorage storage;
    private final UploadChecker uploads;
    private final UserRepository users;
    private final AuditService audit;
    private final QuizQuestionRepository quizQuestions;
    private final ProctorService proctor;
    private final MixedLifecycle mixed;

    public SubmissionService(AssessmentRepository assessments, CourseRepository courses,
            EnrollmentRepository enrollments, CourseFileRepository courseFiles, SubmissionRepository submissions,
            SubmissionFileRepository files, FileStorage storage, UploadChecker uploads, UserRepository users,
            AuditService audit, QuizQuestionRepository quizQuestions, ProctorService proctor, MixedLifecycle mixed) {
        this.mixed = mixed;
        this.quizQuestions = quizQuestions;
        this.proctor = proctor;
        this.users = users;
        this.assessments = assessments;
        this.courses = courses;
        this.enrollments = enrollments;
        this.courseFiles = courseFiles;
        this.submissions = submissions;
        this.files = files;
        this.storage = storage;
        this.uploads = uploads;
        this.audit = audit;
    }

    /** Bài kiểm tra có giám sát: đề chỉ có khi sinh viên đang ở phòng thi trên máy này (proctor.status active). */
    @Transactional(readOnly = true)
    public StudentAssessment view(String assessmentId, AuthPrincipal me, String ip) {
        Target t = requireEnrolled(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = Instant.now();
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId()).orElse(null);
        Course c = t.course();
        // Bài tổng hợp: đề phần nộp file chỉ hiện sau khi bấm "Bắt đầu" (đồng hồ tính chung cả bài)
        boolean started = !a.isMixed() || (s != null && s.getStartedAt() != null);
        return new StudentAssessment(new CourseRef(c.getPublicId(), c.displayCode(), c.getTitle()),
                assessmentView(a, now, started && proctor.paperAllowed(a, me)), submissionView(a, s, now), now,
                proctor.studentState(a, me, ip));
    }

    /** Trạng thái và điểm của người gọi ở mọi bài của khóa học đã ghi danh (mọi bài đều có một dòng). */
    @Transactional(readOnly = true)
    public List<MyResult> myResults(String courseId, AuthPrincipal me) {
        Course c = courses.findByPublicId(courseId)
                .filter(x -> enrollments.existsByCourseIdAndUserId(x.getId(), me.userId()))
                .orElseThrow(() -> AppException.notFound("Course"));
        List<Assessment> list = assessments.findByCourseIdOrderByCreatedAt(c.getId());
        if (list.isEmpty()) {
            return List.of();
        }
        Map<Long, Submission> byAssessment = submissions
                .findByUserIdAndAssessmentIdIn(me.userId(), list.stream().map(Assessment::getId).toList()).stream()
                .collect(Collectors.toMap(Submission::getAssessmentId, Function.identity()));
        Map<Long, Long> fileCounts = byAssessment.isEmpty() ? Map.of()
                : files.countBySubmissionIds(byAssessment.values().stream().map(Submission::getId).toList())
                        .stream().collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));
        Instant now = Instant.now();
        return list.stream().map(a -> {
            Submission s = byAssessment.get(a.getId());
            int count = s == null ? 0 : fileCounts.getOrDefault(s.getId(), 0L).intValue();
            // Điểm chưa công bố (công bố sau giờ đóng) thì không gửi
            return new MyResult(a.getPublicId(), statusOf(a, s, count, now), s == null ? null : s.getSubmittedAt(),
                    s == null || !a.isScoreVisible(now) ? null : s.getScore());
        }).toList();
    }

    /** noRollbackFor: bài tổng hợp vừa quá hạn thì được chốt (MixedLifecycle) rồi mới báo SUBMISSION_LOCKED. */
    @Transactional(noRollbackFor = AppException.class)
    public SubmissionView upload(String assessmentId, MultipartFile upload, AuthPrincipal me) {
        Target t = requireEnrolled(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = Instant.now();
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId()).orElse(null);
        requireEditable(a, s, me, now);
        Checked file = uploads.check(upload, UploadChecker.SUBMISSION);
        if (s == null) {
            s = submissions.save(new Submission(a.getId(), me.userId()));
        } else if (files.countBySubmissionId(s.getId()) >= MAX_FILES) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Too many files");
        }
        String key = storage.store(file.data());
        try {
            files.save(new SubmissionFile(s.getId(), file.name(), file.contentType(), file.data().length, key));
            s.touch();
        } catch (RuntimeException e) {
            storage.deleteQuietly(key);
            throw e;
        }
        // Bằng chứng khi có tranh chấp "em đã tải bài lên rồi"
        audit.record(me.userId(), AuditService.SUBMISSION_FILE_ADDED, t.course().displayCode(),
                a.getTitle() + " · " + file.name() + " (" + file.data().length + " B)");
        return submissionView(a, s, now);
    }

    @Transactional(noRollbackFor = AppException.class)
    public SubmissionView deleteFile(String assessmentId, String fileId, AuthPrincipal me) {
        Target t = requireEnrolled(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = Instant.now();
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId())
                .orElseThrow(() -> AppException.notFound("File"));
        requireEditable(a, s, me, now);
        SubmissionFile f = files.findByPublicId(fileId).filter(x -> x.getSubmissionId().equals(s.getId()))
                .orElseThrow(() -> AppException.notFound("File"));
        files.delete(f);
        s.touch();
        CourseService.afterCommit(() -> storage.deleteQuietly(f.getStorageKey()));
        audit.record(me.userId(), AuditService.SUBMISSION_FILE_REMOVED, t.course().displayCode(),
                a.getTitle() + " · " + f.getName());
        return submissionView(a, s, now);
    }

    /** Nộp bài: cần ít nhất một file; sau khi nộp không thêm/xóa file được nữa. */
    @Transactional
    public SubmissionView submit(String assessmentId, AuthPrincipal me, String ip) {
        Target t = requireEnrolled(assessmentId, me);
        Assessment a = t.assessment();
        Instant now = Instant.now();
        if (a.isMixed()) {
            // Bài tổng hợp nộp chung cả bài qua /student/assessments/{id}/mixed/submit
            throw new AppException(ErrorCode.BAD_REQUEST, "Mixed assessment: submit the whole assessment");
        }
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), me.userId()).orElse(null);
        requireEditable(a, s, me, now);
        long count = s == null ? 0 : files.countBySubmissionId(s.getId());
        if (count == 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Nothing to submit");
        }
        s.submit(now);
        proctor.onWorkFinished(a, me.userId(), "submitted");
        audit.record(me.userId(), AuditService.SUBMISSION_SUBMITTED, t.course().displayCode(),
                a.getTitle() + " (" + count + " file)", ip);
        return submissionView(a, s, now);
    }

    /** Chủ bài nộp hoặc người quản lý khóa học tải được; còn lại NOT_FOUND. */
    @Transactional(readOnly = true)
    public Download download(String fileId, AuthPrincipal me) {
        SubmissionFile f = files.findByPublicId(fileId).orElseThrow(() -> AppException.notFound("File"));
        Submission s = submissions.findById(f.getSubmissionId()).orElseThrow(() -> AppException.notFound("File"));
        boolean owner = s.getUserId().equals(me.userId());
        if (!owner) {
            Course c = assessments.findById(s.getAssessmentId()).flatMap(a -> courses.findById(a.getCourseId()))
                    .orElseThrow(() -> AppException.notFound("File"));
            if (!c.isManagedBy(me.userId(), me.role() == Role.ADMIN)) {
                throw AppException.notFound("File");
            }
        }
        return new Download(f, storage.path(f.getStorageKey()));
    }

    // ---------------------------------------------------------------- nội bộ

    private Target requireEnrolled(String assessmentId, AuthPrincipal me) {
        Assessment a = assessments.findByPublicId(assessmentId)
                .orElseThrow(() -> AppException.notFound("Assessment"));
        if (!enrollments.existsByCourseIdAndUserId(a.getCourseId(), me.userId())) {
            throw AppException.notFound("Assessment");
        }
        Course c = courses.findById(a.getCourseId()).orElseThrow(() -> AppException.notFound("Assessment"));
        return new Target(a, c);
    }

    private void requireEditable(Assessment a, Submission s, AuthPrincipal me, Instant now) {
        if (a.isMixed()) {
            if (!a.usesFiles()) {
                throw new AppException(ErrorCode.BAD_REQUEST, "No file part");
            }
            // Đã bắt đầu, chưa nộp cả bài, chưa hết giờ (quá hạn thì chốt luôn)
            mixed.requireInProgress(a, me.userId(), now);
            return;
        }
        if (a.isQuiz() || a.isCode()) {
            // Bài trắc nghiệm / lập trình làm qua /student/assessments/{id}/quiz/**, /code/**, không nộp file
            throw new AppException(ErrorCode.BAD_REQUEST, a.isQuiz() ? "Quiz assessment" : "Code assessment");
        }
        if (!a.isOpened(now)) {
            throw new AppException(ErrorCode.ASSESSMENT_NOT_OPEN);
        }
        if (a.isClosed(now) || (s != null && s.isSubmitted())) {
            throw new AppException(ErrorCode.SUBMISSION_LOCKED);
        }
    }

    /** Như góc nhìn sinh viên ở trang khóa học: đề chỉ có khi bài đã mở. */
    AssessmentView assessmentView(Assessment a, Instant now) {
        return assessmentView(a, now, true);
    }

    /** paperAllowed false: bài có giám sát mà sinh viên chưa ở phòng thi, không gửi đề. */
    private AssessmentView assessmentView(Assessment a, Instant now, boolean paperAllowed) {
        CourseFile paper = a.getPaperFileId() == null || !a.isOpened(now) || !paperAllowed ? null
                : courseFiles.findById(a.getPaperFileId()).orElse(null);
        return CourseViews.assessmentView(a, paper, questionCount(a), now);
    }

    /** Số câu hiện ở thẻ bài: câu trắc nghiệm (cả bài tổng hợp có phần trắc nghiệm), câu lập trình; 0 với nộp file. */
    long questionCount(Assessment a) {
        return a.usesQuiz() ? quizQuestions.countByAssessmentId(a.getId())
                : a.isCode() ? assessments.countCodeProblems(a.getId()) : 0;
    }

    private SubmissionView submissionView(Assessment a, Submission s, Instant now) {
        List<SubmissionFile> fs = s == null ? List.of() : files.findBySubmissionIdOrderByCreatedAt(s.getId());
        String status = statusOf(a, s, fs.size(), now);
        String grader = s == null || s.getGradedBy() == null ? null
                : users.findById(s.getGradedBy()).map(User::getFullName).orElse(null);
        // Bài tổng hợp: thêm/xóa file trong lượt làm bài (đã bắt đầu, chưa nộp, chưa hết giờ)
        boolean editable = a.isMixed() ? a.usesFiles() && MixedLifecycle.isInProgress(a, s, now)
                : SubmissionViews.IN_PROGRESS.equals(status);
        return new SubmissionView(status, s == null ? null : s.getSubmittedAt(), editable, fileViews(fs),
                a.isScoreVisible(now) ? gradeView(s, grader) : null);
    }

    // ---------------------------------------------------------------- dùng chung với GradingService

    /** Trạng thái bài làm theo thời gian của bài và việc đã bấm nộp hay chưa (s null = chưa có bài nộp). */
    public static String statusOf(Assessment a, Submission s, int fileCount, Instant now) {
        if (s != null && s.isSubmitted()) {
            return SubmissionViews.SUBMITTED;
        }
        if (!a.isOpened(now)) {
            return SubmissionViews.NOT_STARTED;
        }
        if (a.isClosed(now)) {
            // Bài trắc nghiệm đã bắt đầu thì các câu đã chọn là bài nộp (QuizSweeper chốt và chấm ngay sau đó)
            boolean hasWork = fileCount > 0 || (s != null && s.getStartedAt() != null);
            return hasWork ? SubmissionViews.AUTO_SUBMITTED : SubmissionViews.MISSED;
        }
        return SubmissionViews.IN_PROGRESS;
    }

    static List<SubmissionFileView> fileViews(List<SubmissionFile> fs) {
        return fs.stream().map(f -> new SubmissionFileView(f.getPublicId(), f.getName(), f.getContentType(),
                f.getSize(), f.getCreatedAt())).toList();
    }

    static GradeView gradeView(Submission s, String graderName) {
        return s == null || !s.isGraded() ? null
                : new GradeView(s.getScore(), s.getFeedback(), s.getGradedAt(), graderName, s.getAutoScore(),
                        s.getManualScore());
    }
}
