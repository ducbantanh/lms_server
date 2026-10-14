package lms.auth.session;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.auth.AuthPrincipal;
import lms.common.RandomTokens;
import lms.config.AppProperties;
import lms.user.User;

/**
 * Quản lý opaque session lưu trong MySQL.
 *
 * Hết hạn = min(lần hoạt động cuối + idleTimeout, lúc tạo + absoluteTimeout).
 * last_seen_at chỉ cập nhật tối đa 1 lần/phút để không ghi DB ở mọi request.
 */
@Service
public class SessionService {

    public static final String REASON_LOGOUT = "logout";
    public static final String REASON_FORCED = "forced";
    public static final String REASON_LOCKED = "locked";
    public static final String REASON_DELETED = "deleted";
    public static final String REASON_PASSWORD_CHANGED = "password_changed";

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);
    private static final Duration TOUCH_INTERVAL = Duration.ofMinutes(1);

    private final SessionRepository repo;
    private final AppProperties.Session cfg;

    public SessionService(SessionRepository repo, AppProperties props) {
        this.repo = repo;
        this.cfg = props.session();
    }

    /** Kết quả tạo session: token gốc chỉ trả về đúng 1 lần để ghi vào cookie. */
    public record Created(String rawToken, AuthPrincipal principal) {
    }

    @Transactional
    public Created create(User user, String ip, String userAgent) {
        Instant now = now();
        String raw = RandomTokens.sessionToken();
        SessionEntity s = new SessionEntity(
                RandomTokens.sha256Hex(raw),
                RandomTokens.csrfToken(),
                user,
                now,
                now.plus(min(cfg.idleTimeout(), cfg.absoluteTimeout())),
                ip,
                userAgent);
        repo.save(s);
        return new Created(raw, toPrincipal(s));
    }

    /** Xác thực token từ cookie. Trả empty nếu token sai, hết hạn, bị revoke hoặc user bị khóa. */
    @Transactional
    public Optional<AuthPrincipal> authenticate(String rawToken) {
        if (rawToken == null || rawToken.length() > 128) {
            return Optional.empty();
        }
        Instant now = now();
        return repo.findValid(RandomTokens.sha256Hex(rawToken), now).map(s -> {
            if (Duration.between(s.getLastSeenAt(), now).compareTo(TOUCH_INTERVAL) > 0) {
                s.setLastSeenAt(now);
                Instant idleExpiry = now.plus(cfg.idleTimeout());
                Instant absoluteExpiry = s.getCreatedAt().plus(cfg.absoluteTimeout());
                s.setExpiresAt(idleExpiry.isBefore(absoluteExpiry) ? idleExpiry : absoluteExpiry);
            }
            return toPrincipal(s);
        });
    }

    @Transactional
    public void revoke(long sessionId, String reason) {
        repo.revoke(sessionId, now(), reason);
    }

    @Transactional
    public int revokeAllForUser(long userId, String reason) {
        return repo.revokeAllForUser(userId, now(), reason);
    }

    @Transactional
    public int revokeOthersForUser(long userId, long keepSessionId, String reason) {
        return repo.revokeOthersForUser(userId, keepSessionId, now(), reason);
    }

    /** Trong danh sách id, trả về những session còn hiệu lực (dùng cho WebSocket sweeper). */
    @Transactional(readOnly = true)
    public Set<Long> filterValid(Collection<Long> sessionIds) {
        if (sessionIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(repo.findValidIds(sessionIds, now()));
    }

    /** Dọn session cũ mỗi ngày lúc 03:30. */
    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void purgeOld() {
        int n = repo.purgeOlderThan(now().minus(Duration.ofDays(7)));
        if (n > 0) {
            log.info("Purged {} old sessions", n);
        }
    }

    private static AuthPrincipal toPrincipal(SessionEntity s) {
        User u = s.getUser();
        return new AuthPrincipal(
                u.getId(),
                u.getPublicId(),
                u.getUsername(),
                u.getFullName(),
                u.getRole(),
                s.getId(),
                s.getCsrfToken(),
                u.isMustChangePassword());
    }

    private static Instant now() {
        // MySQL DATETIME(3) chỉ giữ mili giây
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    private static Duration min(Duration a, Duration b) {
        return a.compareTo(b) <= 0 ? a : b;
    }
}
