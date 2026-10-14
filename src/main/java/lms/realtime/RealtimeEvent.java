package lms.realtime;

import java.time.Instant;

/**
 * Message server -> client qua WebSocket.
 *
 * <pre>{ "type": "presence.changed", "data": {...}, "ts": "2026-10-14T06:00:00Z" }</pre>
 */
public record RealtimeEvent(String type, Object data, Instant ts) {

    /** Client phải xóa trạng thái và quay về trang login. data.reason: logout | forced | locked | ... */
    public static final String SESSION_REVOKED = "session.revoked";
    /** Gửi cho người có quyền presence.view khi một user online/offline. */
    public static final String PRESENCE_CHANGED = "presence.changed";
    /** Gửi ngay khi kết nối thành công. */
    public static final String CONNECTED = "connected";
    public static final String NOTIFICATION_CREATED = "notification.created";
    public static final String PONG = "pong";

    public static RealtimeEvent of(String type, Object data) {
        return new RealtimeEvent(type, data, Instant.now());
    }
}
