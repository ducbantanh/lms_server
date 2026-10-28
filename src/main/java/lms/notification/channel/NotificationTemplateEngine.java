package lms.notification.channel;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Công cụ biên dịch mẫu thông báo tự động (Notification Template Engine).
 * Thay thế an toàn các biến nội suy {variable_name} thành nội dung thực tế.
 */
public final class NotificationTemplateEngine {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");

    private NotificationTemplateEngine() {
    }

    /**
     * Điền các tham số vào chuỗi mẫu thông báo.
     *
     * @param template Chuỗi mẫu (ví dụ: "Chào {student_name}, bài {exam_title} đã có điểm: {score}/10")
     * @param params   Bảng giá trị tương ứng
     * @return Chuỗi văn bản hoàn chỉnh
     */
    public static String render(String template, Map<String, Object> params) {
        if (template == null || template.isBlank()) {
            return "";
        }

        Map<String, Object> safeParams = params != null ? params : Collections.emptyMap();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = safeParams.get(key);
            String replacement = value != null ? Matcher.quoteReplacement(String.valueOf(value)) : "";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
