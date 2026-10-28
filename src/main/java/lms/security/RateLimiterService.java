package lms.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Dịch vụ kiểm soát tần suất truy cập bộ nhớ đệm (In-Memory Sliding Window Rate Limiter).
 * Ngăn chặn tấn công Brute-Force mật khẩu, dò quét mã PIN phòng thi và DoS API.
 */
@Service
public class RateLimiterService {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterService.class);

    private static class WindowBucket {
        private final long windowStartTimeMillis;
        private final AtomicInteger requestCount;

        public WindowBucket(long startTimeMillis) {
            this.windowStartTimeMillis = startTimeMillis;
            this.requestCount = new AtomicInteger(1);
        }

        public int increment() {
            return requestCount.incrementAndGet();
        }

        public long getStartTime() {
            return windowStartTimeMillis;
        }

        public int getCount() {
            return requestCount.get();
        }
    }

    private final Map<String, WindowBucket> buckets = new ConcurrentHashMap<>();

    /**
     * Kiểm tra xem một key (IP hoặc user public ID) có vượt quá giới hạn request trong khung thời gian hay không.
     *
     * @param key                  Khóa định danh (IP, username)
     * @param maxRequestsPerWindow Số lượng request tối đa
     * @param windowSeconds        Khung thời gian (giây)
     * @return true nếu request được chấp nhận; false nếu vượt hạn mức (Rate Limited)
     */
    public boolean tryAcquire(String key, int maxRequestsPerWindow, int windowSeconds) {
        if (key == null || key.isBlank() || maxRequestsPerWindow <= 0 || windowSeconds <= 0) {
            return true;
        }

        long now = System.currentTimeMillis();
        long windowMillis = windowSeconds * 1000L;

        WindowBucket current = buckets.compute(key, (k, existing) -> {
            if (existing == null || (now - existing.getStartTime()) > windowMillis) {
                return new WindowBucket(now);
            }
            existing.increment();
            return existing;
        });

        boolean allowed = current.getCount() <= maxRequestsPerWindow;
        if (!allowed && current.getCount() == maxRequestsPerWindow + 1) {
            log.warn("Rate limit kích hoạt cho key: {} (vượt quá {} req/{}s)", key, maxRequestsPerWindow, windowSeconds);
        }
        return allowed;
    }

    /**
     * Lấy số request hiện tại trong cửa sổ của một key.
     */
    public int getCurrentCount(String key) {
        WindowBucket b = buckets.get(key);
        return b == null ? 0 : b.getCount();
    }

    /**
     * Dọn dẹp các bucket đã hết hạn mỗi 5 phút để giải phóng bộ nhớ RAM.
     */
    @Scheduled(fixedRate = 300_000)
    public void sweepExpiredBuckets() {
        long now = System.currentTimeMillis();
        int initialSize = buckets.size();
        buckets.entrySet().removeIf(entry -> (now - entry.getValue().getStartTime()) > 600_000);
        int cleared = initialSize - buckets.size();
        if (cleared > 0) {
            log.debug("RateLimiter: Đã dọn dẹp {} bucket hết hạn bộ nhớ đệm", cleared);
        }
    }
}
