package lms.mixed;

import java.time.Instant;

import org.springframework.stereotype.Component;

import lms.audit.AuditService;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Assessment;
import lms.course.Course;
import lms.course.CourseRepository;
import lms.proctor.ProctorService;
import lms.submission.Submission;
import lms.submission.SubmissionRepository;
import lms.user.User;
import lms.user.UserRepository;

/**
 * Lượt làm bài tổng hợp: một lần bắt đầu (submissions.started_at), hạn chung cho mọi phần (Assessment.deadlineFor),
 * nộp hoặc hết giờ thì chốt cả bài (mọi phần cùng khóa) và tính điểm máy chấm. QuizService, CodeService,
 * SubmissionService dùng requireInProgress trước khi nhận câu trả lời, lần nộp code, file của bài tổng hợp.
 */
@Component
public class MixedLifecycle {

    private final SubmissionRepository submissions;
    private final MixedScorer scorer;
    private final ProctorService proctor;
    private final AuditService audit;
    private final CourseRepository courses;
    private final UserRepository users;

    public MixedLifecycle(SubmissionRepository submissions, MixedScorer scorer, ProctorService proctor,
            AuditService audit, CourseRepository courses, UserRepository users) {
        this.submissions = submissions;
        this.scorer = scorer;
        this.proctor = proctor;
        this.audit = audit;
        this.courses = courses;
        this.users = users;
    }

    /** Đã bắt đầu, chưa chốt và chưa quá hạn. */
    public static boolean isInProgress(Assessment a, Submission s, Instant now) {
        if (s == null || s.getStartedAt() == null || s.isSubmitted()) {
            return false;
        }
        Instant deadline = a.deadlineFor(s.getStartedAt());
        return deadline == null || now.isBefore(deadline);
    }

    /**
     * Bài làm đang làm của sinh viên; chưa bắt đầu thì BAD_REQUEST, đã chốt (hoặc vừa quá hạn, chốt luôn) thì
     * SUBMISSION_LOCKED. Gọi trong transaction có noRollbackFor = AppException.class để việc chốt được lưu.
     */
    public Submission requireInProgress(Assessment a, long userId, Instant now) {
        Submission s = submissions.findByAssessmentIdAndUserId(a.getId(), userId)
                .filter(x -> x.getStartedAt() != null)
                .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST, "Assessment not started"));
        finalizeIfDue(a, s, now);
        if (s.isSubmitted()) {
            throw new AppException(ErrorCode.SUBMISSION_LOCKED);
        }
        return s;
    }

    /** Đã bắt đầu, chưa chốt mà quá hạn thì chốt với giờ nộp = hạn; true nếu vừa chốt. */
    public boolean finalizeIfDue(Assessment a, Submission s, Instant now) {
        if (s.getStartedAt() == null || s.isSubmitted()) {
            return false;
        }
        Instant deadline = a.deadlineFor(s.getStartedAt());
        if (deadline == null || now.isBefore(deadline)) {
            return false;
        }
        finalize(a, s, deadline, "timed_out");
        String course = courses.findById(a.getCourseId()).map(Course::displayCode).orElse(null);
        String student = users.findById(s.getUserId()).map(User::getUsername).orElse("?");
        audit.record(null, AuditService.QUIZ_TIMED_OUT, course, a.getTitle() + " · " + student
                + " (tổng hợp, điểm máy chấm " + s.getAutoScore().toPlainString() + ")");
        return true;
    }

    /** Sửa phần lập trình (thêm/xóa câu đổi tổng số câu): tính lại điểm máy chấm mọi bài đã chốt. */
    public void rescoreAll(Assessment a, Instant at) {
        if (a.isMixed()) {
            submissions.findByAssessmentId(a.getId()).forEach(s -> scorer.rescore(a, s, at));
        }
    }

    /** Chốt cả bài và chấm phần tự động; bài có giám sát thì kết thúc phiên thi (reason: submitted / timed_out). */
    public void finalize(Assessment a, Submission s, Instant at, String reason) {
        s.submit(at);
        s.touch();
        scorer.rescore(a, s, at);
        proctor.onWorkFinished(a, s.getUserId(), reason);
    }
}
