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
    /** Gửi cho giảng viên phụ trách khi sinh viên quét QR điểm danh (cập nhật màn chiếu và danh sách). */
    public static final String ATTENDANCE_CHECKED_IN = "attendance.checked_in";
    /**
     * Gửi sinh viên nộp bài lập trình (và giảng viên phụ trách nếu là lần nộp) khi một lần chạy/nộp chấm xong;
     * chỉ báo có kết quả mới, client tải lại qua API.
     */
    public static final String CODE_JUDGED = "code.judged";
    /** Gửi giảng viên phụ trách khi một dòng của bảng giám thị đổi (vào phòng thi, vi phạm, khóa, nộp bài). */
    public static final String PROCTOR_UPDATED = "proctor.updated";
    /** Gửi sinh viên khi giám thị khóa / mở khóa / cho đổi máy; client tải lại trạng thái phòng thi. */
    public static final String PROCTOR_STATE = "proctor.state";
    public static final String PONG = "pong";

    public static RealtimeEvent of(String type, Object data) {
        return new RealtimeEvent(type, data, Instant.now());
    }
}
