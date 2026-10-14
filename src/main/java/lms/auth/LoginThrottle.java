package lms.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lms.config.AppProperties;

/**
 * Giới hạn đăng nhập sai theo cặp (username, IP). Lưu trong RAM vì server chạy 1 process.
 * Sau N lần sai liên tiếp -> khóa tạm thời trong một khoảng thời gian.
 */
@Component
public class LoginThrottle {

    private record Entry(int failures, Instant lockedUntil, Instant lastFailure) {
    }

    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final Duration lockDuration;

    public LoginThrottle(AppProperties props) {
        this.maxAttempts = props.security().loginMaxAttempts();
        this.lockDuration = props.security().loginLockDuration();
    }

    public boolean isBlocked(String username, String ip) {
        Entry e = entries.get(key(username, ip));
        return e != null && e.lockedUntil() != null && e.lockedUntil().isAfter(Instant.now());
    }

    public void recordFailure(String username, String ip) {
        Instant now = Instant.now();
        entries.compute(key(username, ip), (k, e) -> {
            int failures = (e == null || expired(e, now)) ? 1 : e.failures() + 1;
            Instant lockedUntil = failures >= maxAttempts ? now.plus(lockDuration) : null;
            return new Entry(failures, lockedUntil, now);
        });
    }

    public void recordSuccess(String username, String ip) {
        entries.remove(key(username, ip));
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void cleanup() {
        Instant now = Instant.now();
        entries.entrySet().removeIf(en -> expired(en.getValue(), now));
    }

    /** Một entry hết tác dụng nếu không còn bị khóa và lần sai cuối đã quá lockDuration. */
    private boolean expired(Entry e, Instant now) {
        boolean locked = e.lockedUntil() != null && e.lockedUntil().isAfter(now);
        return !locked && e.lastFailure().plus(lockDuration).isBefore(now);
    }

    private static String key(String username, String ip) {
        return (username == null ? "" : username.toLowerCase(Locale.ROOT)) + "|" + ip;
    }
}
