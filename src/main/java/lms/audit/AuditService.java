package lms.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lms.common.RequestInfo;

/**
 * Ghi audit log cho mọi thao tác thay đổi dữ liệu (login, admin, giảng viên, nộp bài).
 * Không ghi trạng thái cá nhân như đánh dấu đã học, đã đọc thông báo.
 * Quy ước: target là đối tượng chính (username, mã khóa học), detail là mô tả ngắn.
 * KHÔNG ghi mật khẩu, cookie, token vào detail.
 */
@Service
public class AuditService {

    public static final String LOGIN_SUCCESS = "auth.login";
    public static final String LOGIN_FAILED = "auth.login_failed";
    public static final String LOGOUT = "auth.logout";
    public static final String PASSWORD_CHANGED = "auth.password_changed";
    public static final String USER_LOCKED = "user.locked";
    public static final String USER_UNLOCKED = "user.unlocked";
    public static final String USER_DELETED = "user.deleted";
    public static final String USER_UPDATED = "user.updated";
    /** Người dùng tự sửa thông tin liên hệ / giới thiệu ở trang hồ sơ. */
    public static final String PROFILE_UPDATED = "user.profile_updated";
    public static final String PASSWORD_RESET = "user.password_reset";
    public static final String USERS_IMPORTED = "user.imported";
    public static final String FORCE_LOGOUT = "session.force_logout";
    public static final String IDLE_TIMEOUT_UPDATED = "session.idle_timeout_updated";
    public static final String COURSE_CREATED = "course.created";
    public static final String COURSE_UPDATED = "course.updated";
    public static final String COURSE_ASSIGNED = "course.assigned";
    public static final String COURSE_DELETED = "course.deleted";
    public static final String SECTION_CREATED = "section.created";
    public static final String SECTION_UPDATED = "section.updated";
    public static final String SECTION_DELETED = "section.deleted";
    public static final String SECTIONS_REORDERED = "section.reordered";
    public static final String LESSON_CREATED = "lesson.created";
    public static final String LESSON_UPDATED = "lesson.updated";
    public static final String LESSON_DELETED = "lesson.deleted";
    public static final String LESSONS_REORDERED = "lesson.reordered";
    public static final String FILE_UPLOADED = "file.uploaded";
    public static final String FILE_HIDDEN = "file.hidden";
    public static final String FILE_SHOWN = "file.shown";
    public static final String FILE_DELETED = "file.deleted";
    public static final String ASSESSMENT_CREATED = "assessment.created";
    public static final String ASSESSMENT_UPDATED = "assessment.updated";
    public static final String ASSESSMENT_DELETED = "assessment.deleted";
    public static final String STUDENTS_ENROLLED = "enrollment.added";
    public static final String STUDENT_UNENROLLED = "enrollment.removed";
    public static final String COURSE_JOINED = "enrollment.joined";
    public static final String ENROLL_KEY_CREATED = "enrollment.key_created";
    public static final String ENROLL_KEY_DISABLED = "enrollment.key_disabled";
    public static final String NOTIFICATION_SENT = "notification.sent";
    public static final String SUBMISSION_FILE_ADDED = "submission.file_added";
    public static final String SUBMISSION_FILE_REMOVED = "submission.file_removed";
    public static final String SUBMISSION_SUBMITTED = "submission.submitted";
    public static final String SUBMISSION_GRADED = "submission.graded";
    public static final String SUBMISSIONS_DOWNLOADED = "submission.downloaded";
    public static final String GRADES_EXPORTED = "grade.exported";
    public static final String ATTENDANCE_SESSION_CREATED = "attendance.session_created";
    public static final String ATTENDANCE_SESSION_UPDATED = "attendance.session_updated";
    public static final String ATTENDANCE_SESSION_DELETED = "attendance.session_deleted";
    public static final String ATTENDANCE_QR_OPENED = "attendance.qr_opened";
    public static final String ATTENDANCE_QR_CLOSED = "attendance.qr_closed";
    public static final String ATTENDANCE_CHECKED_IN = "attendance.checked_in";
    public static final String ATTENDANCE_MARKED = "attendance.marked";
    public static final String ATTENDANCE_EXPORTED = "attendance.exported";
    public static final String QUESTION_BANK_CREATED = "question_bank.created";
    public static final String QUESTION_BANK_UPDATED = "question_bank.updated";
    public static final String QUESTION_BANK_DELETED = "question_bank.deleted";
    public static final String QUESTION_CREATED = "question.created";
    public static final String QUESTION_UPDATED = "question.updated";
    public static final String QUESTIONS_DELETED = "question.deleted";
    public static final String QUESTIONS_IMPORTED = "question.imported";
    /** Kho bài lập trình của giảng viên (target: tên câu, hoặc số câu khi thêm/xóa nhiều câu). */
    public static final String CODE_PROBLEMS_ADDED = "code_problem.added";
    public static final String CODE_PROBLEM_UPDATED = "code_problem.updated";
    public static final String CODE_PROBLEMS_DELETED = "code_problem.deleted";
    public static final String QUIZ_STARTED = "submission.quiz_started";
    /** Hết giờ làm bài trắc nghiệm mà chưa bấm nộp: server tự chốt và chấm (không có người thực hiện). */
    public static final String QUIZ_TIMED_OUT = "submission.quiz_timed_out";
    /** Giảng viên bấm chấm lại mọi lần nộp của bài lập trình (sửa bộ test thì ghi assessment.updated). */
    public static final String CODE_REJUDGED = "submission.code_rejudged";

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository repo;
    private final TransactionTemplate tx;

    public AuditService(AuditLogRepository repo, PlatformTransactionManager txManager) {
        this.repo = repo;
        this.tx = new TransactionTemplate(txManager);
        // Transaction riêng: audit vẫn được ghi kể cả khi nghiệp vụ bên ngoài rollback
        this.tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /** Như record(..., ip) với IP của request đang xử lý (null nếu gọi ngoài request). */
    public void record(Long actorUserId, String action, String target, String detail) {
        record(actorUserId, action, target, detail,
                RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs
                        ? RequestInfo.clientIp(attrs.getRequest())
                        : null);
    }

    public void record(Long actorUserId, String action, String target, String detail, String ip) {
        try {
            tx.executeWithoutResult(status -> repo.save(new AuditLog(actorUserId, action, target, detail, ip)));
        } catch (RuntimeException e) {
            // Audit lỗi không được làm hỏng request chính
            log.warn("Failed to write audit log {}: {}", action, e.getMessage());
        }
    }
}
