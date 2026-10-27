package lms.notification;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO thông báo, khớp lms_client/lib/notifications.ts; mọi id trả ra ngoài là public_id. */
public final class NotificationViews {

    private NotificationViews() {
    }

    /** Tên loại trong API, theo chỉ số Notification.CATEGORY_*. */
    static final List<String> CATEGORIES = List.of("exam", "lab", "practice", "content", "announcement",
            "schedule", "leave");

    public record CourseRef(String id, String code, String title) {
    }

    /** assessmentId có giá trị khi thông báo về một bài; senderName null nếu tài khoản người gửi đã bị xóa. */
    public record NotificationView(String id, String category, String title, String body, CourseRef course,
            String assessmentId, String senderName, Instant createdAt, boolean read) {
    }

    /** Số chưa đọc: tổng và theo từng loại (loại không có thì không có khóa). */
    public record Summary(long unread, Map<String, Long> byCategory) {
    }

    /** Thông báo đã gửi của khóa học, cho giảng viên: số người nhận và số đã đọc. */
    public record SentView(String id, String category, String title, String body, String senderName,
            Instant createdAt, long recipients, long read) {
    }

    /** studentIds null/rỗng = gửi cả lớp. */
    public record AnnouncementRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String body,
            @Size(max = 1000) List<String> studentIds) {
    }
}
