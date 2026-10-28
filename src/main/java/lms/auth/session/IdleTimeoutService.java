package lms.auth.session;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lms.audit.AuditService;
import lms.auth.AuthPrincipal;
import lms.auth.Role;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.config.AppProperties;

/**
 * Thời gian tự đăng xuất khi không hoạt động, đặt riêng theo vai trò (cột roles.idle_timeout_minutes).
 * Đọc mỗi lần gia hạn phiên nên giữ trong bộ nhớ; vai trò chưa đặt riêng dùng app.session.idle-timeout.
 * Admin chỉnh cho sinh viên và giảng viên ở trang Cài đặt; đổi xong áp dụng ngay cho phiên đang mở.
 */
@Service
public class IdleTimeoutService {

    public static final int MIN_MINUTES = 1;

    private static final Logger log = LoggerFactory.getLogger(IdleTimeoutService.class);

    private final JdbcTemplate jdbc;
    private final SessionRepository sessions;
    private final AuditService audit;
    private final Duration fallback;
    private final Duration absolute;
    private volatile Map<Role, Duration> cache = Map.of();

    public IdleTimeoutService(JdbcTemplate jdbc, SessionRepository sessions, AuditService audit,
            AppProperties props) {
        this.jdbc = jdbc;
        this.sessions = sessions;
        this.audit = audit;
        this.fallback = props.session().idleTimeout();
        this.absolute = props.session().absoluteTimeout();
    }

    /** Giá trị trang admin đọc/ghi; min/max để form kiểm tra trước khi gửi. */
    public record Settings(int studentMinutes, int teacherMinutes, int minMinutes, int maxMinutes) {
    }

    public record UpdateRequest(@NotNull Integer studentMinutes, @NotNull Integer teacherMinutes) {
    }

    @EventListener(ApplicationReadyEvent.class)
    public void reload() {
        Map<Role, Duration> map = new EnumMap<>(Role.class);
        jdbc.query("SELECT id, idle_timeout_minutes FROM roles WHERE idle_timeout_minutes IS NOT NULL",
                rs -> {
                    map.put(Role.fromCode(rs.getInt("id")), Duration.ofMinutes(rs.getInt("idle_timeout_minutes")));
                });
        this.cache = Collections.unmodifiableMap(map);
        log.info("Loaded idle timeouts {}", map);
    }

    public Duration idleTimeout(Role role) {
        return cache.getOrDefault(role, fallback);
    }

    public Settings settings() {
        return new Settings(
                (int) idleTimeout(Role.STUDENT).toMinutes(),
                (int) idleTimeout(Role.TEACHER).toMinutes(),
                MIN_MINUTES,
                maxMinutes());
    }

    @Transactional
    public Settings update(UpdateRequest body, AuthPrincipal me, String ip) {
        int student = checked(body.studentMinutes());
        int teacher = checked(body.teacherMinutes());
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        save(Role.STUDENT, student, now);
        save(Role.TEACHER, teacher, now);
        audit.record(me.userId(), AuditService.IDLE_TIMEOUT_UPDATED, "idle-timeout",
                "student=" + student + "m; teacher=" + teacher + "m", ip);
        // Cập nhật bộ nhớ khi DB đã commit, tránh lệch nếu transaction bị rollback
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                reload();
            }
        });
        return new Settings(student, teacher, MIN_MINUTES, maxMinutes());
    }

    /** Ghi DB rồi tính lại hạn của phiên đang mở theo lần hoạt động cuối, để thay đổi có hiệu lực ngay. */
    private void save(Role role, int minutes, Instant now) {
        jdbc.update("UPDATE roles SET idle_timeout_minutes = ? WHERE id = ?", minutes, role.code());
        sessions.recomputeExpiry(role.code(), Duration.ofMinutes(minutes).toSeconds(), absolute.toSeconds(), now);
    }

    /** Không vượt thời hạn tuyệt đối của phiên (app.session.absolute-timeout). */
    private int maxMinutes() {
        return (int) absolute.toMinutes();
    }

    private int checked(int minutes) {
        if (minutes < MIN_MINUTES || minutes > maxMinutes()) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Idle timeout must be between " + MIN_MINUTES + " and " + maxMinutes() + " minutes");
        }
        return minutes;
    }
}
