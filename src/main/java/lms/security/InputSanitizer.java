package lms.security;

import java.util.regex.Pattern;

/**
 * Tiện ích làm sạch và trung hòa nội dung văn bản độc hại (XSS & Injection Sanitizer).
 */
public final class InputSanitizer {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script.*?>.*?</script.*?>");
    private static final Pattern STYLE_PATTERN = Pattern.compile("(?i)<style.*?>.*?</style.*?>");
    private static final Pattern TAGS_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern JAVASCRIPT_SCHEME = Pattern.compile("(?i)javascript:");
    private static final Pattern EVENT_HANDLERS = Pattern.compile("(?i)\\bon\\w+\\s*=");

    private InputSanitizer() {
    }

    /**
     * Loại bỏ hoàn toàn các thẻ HTML, thẻ script, CSS inline và thuộc tính bắt sự kiện (onclick, onload...).
     */
    public static String stripHtml(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String cleaned = SCRIPT_PATTERN.matcher(input).replaceAll("");
        cleaned = STYLE_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = TAGS_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = JAVASCRIPT_SCHEME.matcher(cleaned).replaceAll("");
        cleaned = EVENT_HANDLERS.matcher(cleaned).replaceAll("");
        return cleaned.trim();
    }

    /**
     * Mã hóa các ký tự đặc biệt của HTML (&, <, >, ", ') thành HTML Entities.
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '\"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#x27;");
                case '/' -> sb.append("&#x2F;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Kiểm tra xem chuỗi đầu vào có dấu hiệu mã độc XSS hay không.
     */
    public static boolean containsSuspiciousContent(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        return SCRIPT_PATTERN.matcher(input).find()
                || JAVASCRIPT_SCHEME.matcher(input).find()
                || EVENT_HANDLERS.matcher(input).find()
                || input.contains("<iframe")
                || input.contains("<object")
                || input.contains("<embed");
    }
}
