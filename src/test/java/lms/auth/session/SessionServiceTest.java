package lms.auth.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class SessionServiceTest {

    private static final Instant CREATED = Instant.parse("2026-10-23T08:00:00Z");
    private static final Duration ABSOLUTE = Duration.ofHours(12);

    @Test
    void expiryIsLastActivityPlusIdleTimeout() {
        Instant seen = CREATED.plus(Duration.ofMinutes(30));
        assertEquals(seen.plus(Duration.ofMinutes(5)),
                SessionService.expiry(seen, CREATED, Duration.ofMinutes(5), ABSOLUTE));
    }

    @Test
    void expiryNeverPassesAbsoluteTimeout() {
        Instant seen = CREATED.plus(Duration.ofHours(11)).plus(Duration.ofMinutes(58));
        assertEquals(CREATED.plus(ABSOLUTE),
                SessionService.expiry(seen, CREATED, Duration.ofMinutes(5), ABSOLUTE));
    }

    @Test
    void idleExpiryOnlyWhenExpiredBeforeAbsoluteLimit() {
        Instant idleExpiry = CREATED.plus(Duration.ofMinutes(10));
        // Đã quá hạn vì không hoạt động
        assertTrue(SessionService.isIdleExpiry(idleExpiry, CREATED, idleExpiry, ABSOLUTE));
        // Chưa tới hạn
        assertFalse(SessionService.isIdleExpiry(idleExpiry, CREATED, idleExpiry.minusSeconds(1), ABSOLUTE));
        // Hết hạn vì đủ 12 giờ, không phải vì không hoạt động
        Instant absoluteExpiry = CREATED.plus(ABSOLUTE);
        assertFalse(SessionService.isIdleExpiry(absoluteExpiry, CREATED, absoluteExpiry, ABSOLUTE));
    }
}
