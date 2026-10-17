package lms.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Ghi audit log cho thao tác quan trọng (login, admin action).
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
    public static final String USERS_IMPORTED = "user.imported";
    public static final String FORCE_LOGOUT = "session.force_logout";
    public static final String COURSE_CREATED = "course.created";
    public static final String COURSE_DELETED = "course.deleted";

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository repo;
    private final TransactionTemplate tx;

    public AuditService(AuditLogRepository repo, PlatformTransactionManager txManager) {
        this.repo = repo;
        this.tx = new TransactionTemplate(txManager);
        // Transaction riêng: audit vẫn được ghi kể cả khi nghiệp vụ bên ngoài rollback
        this.tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
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
