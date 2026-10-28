package lms.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterServiceTest {

    @Test
    @DisplayName("RateLimiterService cho phép request trong ngưỡng và chặn khi vượt quá")
    void testRateLimiter() {
        RateLimiterService limiter = new RateLimiterService();
        String clientIp = "192.168.1.100";

        // Cho phép 3 request / 10 giây
        assertThat(limiter.tryAcquire(clientIp, 3, 10)).isTrue();
        assertThat(limiter.tryAcquire(clientIp, 3, 10)).isTrue();
        assertThat(limiter.tryAcquire(clientIp, 3, 10)).isTrue();

        // Lần thứ 4 bị chặn
        assertThat(limiter.tryAcquire(clientIp, 3, 10)).isFalse();
        assertThat(limiter.getCurrentCount(clientIp)).isEqualTo(4);

        // IP khác vẫn được phép
        assertThat(limiter.tryAcquire("192.168.1.101", 3, 10)).isTrue();
    }
}
