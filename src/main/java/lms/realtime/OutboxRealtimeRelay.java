package lms.realtime;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Process không giữ WebSocket (máy chấm riêng): ghi sự kiện vào realtime_outbox trong cùng transaction với dữ liệu,
 * nên sự kiện chỉ tới tay server web khi dữ liệu đã commit. Không phải @Component: chỉ JudgeWorkerApplication nạp.
 */
public class OutboxRealtimeRelay implements RealtimeRelay {

    private final RealtimeOutboxRepository outbox;
    private final ObjectMapper mapper;

    public OutboxRealtimeRelay(RealtimeOutboxRepository outbox, ObjectMapper mapper) {
        this.outbox = outbox;
        this.mapper = mapper;
    }

    @Override
    public void sendAfterCommit(Collection<Long> userIds, String type, Object data) {
        String payload;
        try {
            payload = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize realtime event " + type, e);
        }
        outbox.saveAll(userIds.stream().distinct().map(u -> new RealtimeOutbox(u, type, payload)).toList());
    }
}
