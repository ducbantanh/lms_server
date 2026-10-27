package lms.realtime;

import java.util.Collection;

/**
 * Gửi sự kiện realtime cho user sau khi transaction hiện tại commit, kể cả từ process không giữ WebSocket.
 * Server web: DirectRealtimeRelay gửi thẳng qua RealtimeHub. Process máy chấm riêng (app.judge.mode=worker):
 * OutboxRealtimeRelay ghi vào bảng realtime_outbox, server web (mode external) đọc và gửi (RealtimeOutboxPoller).
 */
public interface RealtimeRelay {

    void sendAfterCommit(Collection<Long> userIds, String type, Object data);
}
