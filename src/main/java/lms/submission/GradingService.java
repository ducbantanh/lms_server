package lms.submission;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import lms.course.CourseService;
import lms.course.CourseViews;
import lms.course.CourseViews.AssessmentView;
import lms.course.EnrollmentRepository;
import lms.quiz.QuizQuestionRepository;
import lms.submission.SubmissionViews.AssessmentSubmissions;
import lms.submission.SubmissionViews.CourseRef;
import lms.submission.SubmissionViews.GradeRequest;
import lms.submission.SubmissionViews.SubmissionRow;
import lms.submission.SubmissionViews.SubmissionSummary;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Giảng viên xem bài nộp của một bài và chấm điểm thủ công (thang 10 + nhận xét).
 * Admin xem được danh sách bài nộp nhưng chỉ giảng viên phụ trách mới chấm (TEACHER_ONLY).
 * Chỉ chấm bài đã chốt: đã bấm nộp, hết giờ tự tính bài nộp, hoặc hết giờ không nộp (chấm 0 được).
 * Khóa học không thuộc quyền quản lý trả NOT_FOUND như các API giảng viên khác.
 */
@Service
public class GradingService {

    /** Trạng thái được chấm: bài làm không còn thay đổi được nữa. */
    private static final Set<String> GRADABLE = Set.of(SubmissionViews.SUBMITTED,
            SubmissionViews.AUTO_SUBMITTED, SubmissionViews.MISSED);

    private final CourseService courses;
    private final AssessmentRepository assessments;
    private final CourseFileRepository courseFiles;
    private final EnrollmentRepository enrollments;
    private final SubmissionRepository submissions;
    private final SubmissionFileRepository files;
    private final UserRepository users;
    private final AuditService audit;
    private final QuizQuestionRepository quizQuestions;

    public GradingService(CourseService courses, AssessmentRepository assessments, CourseFileRepository courseFiles,
            EnrollmentRepository enrollments, SubmissionRepository submissions, SubmissionFileRepository files,
            UserRepository users, AuditService audit, QuizQuestionRepository quizQuestions) {
        this.quizQuestions = quizQuestions;
        this.courses = courses;
        this.assessments = assessments;
        this.courseFiles = courseFiles;
        this.enrollments = enrollments;
        this.submissions = submissions;
        this.files = files;
        this.users = users;
        this.audit = audit;
    }

    /** Mọi sinh viên đã ghi danh (sắp theo MSSV) kèm bài nộp, file và điểm; bài nộp của người đã rời lớp bị bỏ qua. */
    @Transactional(readOnly = true)
    public AssessmentSubmissions list(String courseId, String assessmentId, AuthPrincipal me) {
        Course c = courses.requireManaged(courseId, me);
        Assessment a = requireAssessment(c, assessmentId);
        Instant now = Instant.now();
        Map<Long, Submission> byUser = submissions.findByAssessmentId(a.getId()).stream()
                .collect(Collectors.toMap(Submission::getUserId, Function.identity()));
        Map<Long, List<SubmissionFile>> filesBySubmission = byUser.isEmpty() ? Map.of()
                : files.findBySubmissionIdInOrderByCreatedAt(byUser.values().stream().map(Submission::getId).toList())
                        .stream().collect(Collectors.groupingBy(SubmissionFile::getSubmissionId));
        Map<Long, String> graders = graderNames(byUser.values());

        List<SubmissionRow> rows = enrollments.findWithUsers(c.getId()).stream().map(row -> {
            User u = (User) row[1];
            return row(a, u, byUser.get(u.getId()), filesBySubmission, graders, now);
        }).toList();
        return new AssessmentSubmissions(new CourseRef(c.getPublicId(), c.displayCode(), c.getTitle()),
                teacherView(a, now), summary(rows), rows, CourseService.isTeacherOf(c, me));
    }

    /** Chấm (score khác null) hoặc xóa điểm (score null) của một sinh viên; trả về dòng đã cập nhật. */
    @Transactional
    public SubmissionRow grade(String courseId, String assessmentId, String studentId, GradeRequest req,
            AuthPrincipal me, String ip) {
        Course c = courses.requireTeaching(courseId, me);
        Assessment a = requireAssessment(c, assessmentId);
        if (a.isCode()) {
            // Bài lập trình không chấm điểm: kết quả là số test đúng của từng câu (lms.code)
            throw new AppException(ErrorCode.BAD_REQUEST, "Code assessment has no score");
        }
        User student = users.findByPublicId(studentId)
                .filter(u -> enrollments.existsByCourseIdAndUserId(c.getId(), u.getId()))
                .orElseThrow(() -> AppException.notFound("Student"));
        Instant now = Instant.now();
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), student.getId()).orElse(null);
        int fileCount = s == null ? 0 : (int) files.countBySubmissionId(s.getId());
        if (!GRADABLE.contains(SubmissionService.statusOf(a, s, fileCount, now))) {
            throw new AppException(ErrorCode.SUBMISSION_NOT_FINAL);
        }
        if (req.score() == null) {
            if (s != null) {
                s.clearGrade();
            }
        } else {
            // Không nộp vẫn chấm được: tạo bài nộp rỗng để giữ điểm
            if (s == null) {
                s = submissions.save(new Submission(a.getId(), student.getId()));
            }
            s.grade(req.score().setScale(2, RoundingMode.UNNECESSARY), CourseService.blankToNull(req.feedback()),
                    me.userId(), now);
        }
        submissions.flush();
        audit.record(me.userId(), AuditService.SUBMISSION_GRADED, c.displayCode(),
                a.getTitle() + " · " + student.getUsername() + " = "
                        + (req.score() == null ? "(xóa điểm)" : req.score().toPlainString()),
                ip);
        Map<Long, List<SubmissionFile>> fs = s == null ? Map.of()
                : Map.of(s.getId(), files.findBySubmissionIdOrderByCreatedAt(s.getId()));
        return row(a, student, s, fs, s == null ? Map.of() : graderNames(List.of(s)), now);
    }

    private Assessment requireAssessment(Course c, String assessmentId) {
        return assessments.findByPublicIdAndCourseId(assessmentId, c.getId())
                .orElseThrow(() -> AppException.notFound("Assessment"));
    }

    private static SubmissionRow row(Assessment a, User u, Submission s,
            Map<Long, List<SubmissionFile>> filesBySubmission, Map<Long, String> graders, Instant now) {
        List<SubmissionFile> fs = s == null ? List.of() : filesBySubmission.getOrDefault(s.getId(), List.of());
        return new SubmissionRow(u.getPublicId(), u.getUsername(), u.getFullName(),
                SubmissionService.statusOf(a, s, fs.size(), now), s == null ? null : s.getSubmittedAt(),
                s == null || fs.isEmpty() ? null : s.getUpdatedAt(), SubmissionService.fileViews(fs),
                // Map.of() không cho tra khóa null: bài chưa chấm thì gradedBy null
                SubmissionService.gradeView(s, s == null || s.getGradedBy() == null ? null
                        : graders.get(s.getGradedBy())));
    }

    private static SubmissionSummary summary(List<SubmissionRow> rows) {
        int submitted = (int) rows.stream().filter(r -> SubmissionViews.SUBMITTED.equals(r.status())
                || SubmissionViews.AUTO_SUBMITTED.equals(r.status())).count();
        List<BigDecimal> scores = rows.stream().map(SubmissionRow::grade).filter(Objects::nonNull)
                .map(SubmissionViews.GradeView::score).toList();
        BigDecimal average = scores.isEmpty() ? null
                : scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
        return new SubmissionSummary(rows.size(), submitted, scores.size(), average);
    }

    private Map<Long, String> graderNames(Collection<Submission> list) {
        Set<Long> ids = list.stream().map(Submission::getGradedBy).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return ids.isEmpty() ? Map.of()
                : users.findAllById(ids).stream().collect(Collectors.toMap(User::getId, User::getFullName));
    }

    /** Giảng viên luôn thấy đề, kể cả trước giờ mở. */
    private AssessmentView teacherView(Assessment a, Instant now) {
        CourseFile paper = a.getPaperFileId() == null ? null : courseFiles.findById(a.getPaperFileId()).orElse(null);
        return CourseViews.assessmentView(a, paper, a.isQuiz() ? quizQuestions.countByAssessmentId(a.getId())
                : a.isCode() ? assessments.countCodeProblems(a.getId()) : 0,
                now);
    }
}
