package lms.realtime;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Server web khi máy chấm chạy process riêng (app.judge.mode=external): đọc sự kiện process khác ghi vào
 * realtime_outbox và gửi qua WebSocket. Không đi theo con trỏ id: hai transaction có thể commit lệch thứ tự id
 * (id nhỏ commit sau), con trỏ sẽ bỏ sót. Thay vào đó mỗi lần đọc mọi sự kiện trong WINDOW gần nhất và nhớ id đã gửi
 * (đủ rộng cho commit chậm và lệch đồng hồ giữa các máy). Mỗi server web tự nhớ, nên chạy nhiều server web vẫn đúng.
 */
@Component
@ConditionalOnProperty(name = "app.judge.mode", havingValue = "external")
public class RealtimeOutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(RealtimeOutboxPoller.class);
    static final Duration WINDOW = Duration.ofSeconds(30);
    private static final Duration RETENTION = Duration.ofMinutes(10);
    private static final int BATCH = 2_000;

    private final RealtimeOutboxRepository outbox;
    private final RealtimeHub hub;
    private final ObjectMapper mapper;
    /** id đã gửi (hoặc có sẵn lúc khởi động) → createdAt, chỉ giữ trong WINDOW. */
    private final Map<Long, Instant> seen = new HashMap<>();
    private volatile boolean ready;

    public RealtimeOutboxPoller(RealtimeOutboxRepository outbox, RealtimeHub hub, ObjectMapper mapper) {
        this.outbox = outbox;
        this.hub = hub;
        this.mapper = mapper;
    }

    /** Sự kiện có sẵn lúc khởi động coi như đã gửi: không phát lại sự kiện cũ sau khi server web khởi động lại. */
    @EventListener(ApplicationReadyEvent.class)
    public synchronized void start() {
        outbox.findByCreatedAtAfterOrderByIdAsc(Instant.now().minus(WINDOW), Limit.of(BATCH))
                .forEach(o -> seen.put(o.getId(), o.getCreatedAt()));
        ready = true;
        log.info("Code judge runs in a separate worker process; relaying its realtime events from the outbox");
    }

    @Scheduled(fixedDelay = 300, initialDelay = 1_000)
    public synchronized void poll() {
        if (!ready) {
            return;
        }
        try {
            Instant since = Instant.now().minus(WINDOW);
            for (RealtimeOutbox o : outbox.findByCreatedAtAfterOrderByIdAsc(since, Limit.of(BATCH))) {
                if (seen.putIfAbsent(o.getId(), o.getCreatedAt()) == null) {
                    hub.sendToUser(o.getUserId(),
                            new RealtimeEvent(o.getType(), mapper.readTree(o.getPayload()), o.getCreatedAt()));
                }
            }
            seen.values().removeIf(at -> at.isBefore(since));
        } catch (Exception e) {
            log.warn("Realtime outbox poll failed: {}", e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    @Transactional
    public void cleanup() {
        outbox.deleteOlderThan(Instant.now().minus(RETENTION));
    }
}
