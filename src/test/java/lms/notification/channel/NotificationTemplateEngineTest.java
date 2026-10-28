package lms.notification.channel;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTemplateEngineTest {

    @Test
    @DisplayName("render thay thế chính xác các biến trong mẫu thông báo")
    void testRender() {
        String template = "Xin chào {student_name}, bài thi {exam_title} đã chấm xong với điểm số: {score}/10.";
        Map<String, Object> params = Map.of(
                "student_name", "Nguyễn Đức Nghĩa",
                "exam_title", "Kiểm tra giữa kỳ Lập trình Mạng",
                "score", 9.5
        );

        String result = NotificationTemplateEngine.render(template, params);
        assertThat(result).isEqualTo("Xin chào Nguyễn Đức Nghĩa, bài thi Kiểm tra giữa kỳ Lập trình Mạng đã chấm xong với điểm số: 9.5/10.");
    }

    @Test
    @DisplayName("render xử lý an toàn khi thiếu tham số hoặc chuỗi rỗng")
    void testRenderEdgeCases() {
        assertThat(NotificationTemplateEngine.render("", Map.of())).isEqualTo("");
        assertThat(NotificationTemplateEngine.render(null, Map.of())).isEqualTo("");
        assertThat(NotificationTemplateEngine.render("Hello {name}", Map.of())).isEqualTo("Hello ");
        assertThat(NotificationTemplateEngine.render("Static message", null)).isEqualTo("Static message");
    }
}
