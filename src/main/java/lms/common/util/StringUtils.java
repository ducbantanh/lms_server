package lms.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Tiện ích xử lý chuỗi ký tự nâng cao (Advanced String Utilities).
 * Hỗ trợ bóc tách dấu tiếng Việt, sinh slug URL an toàn, tính toán độ tương đồng chuỗi
 * (phục vụ đối chiếu gian lận / trùng khớp mã nguồn) và làm mờ thông tin nhạy cảm.
 */
public final class StringUtils {

    private static final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");

    private StringUtils() {
    }

    /**
     * Chuyển chuỗi tiếng Việt có dấu thành không dấu (chuẩn Unicode NFD).
     * Ví dụ: "Nguyễn Đức Nghĩa" -> "Nguyen Duc Nghia".
     */
    public static String stripAccents(String input) {
        if (input == null) {
            return "";
        }
        // Thay thế ký tự đ, Đ đặc biệt của tiếng Việt
        String text = input.replace("đ", "d").replace("Đ", "D");
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return DIACRITICS_PATTERN.matcher(normalized).replaceAll("");
    }

    /**
     * Tạo đường dẫn URL an toàn (Slug) từ chuỗi văn bản bất kỳ.
     * Ví dụ: "Lập trình Mạng Căn bản - Kỳ 1" -> "lap-trinh-mang-can-ban-ky-1".
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String noAccents = stripAccents(input).toLowerCase(Locale.ROOT);
        String cleaned = NON_ALPHANUMERIC.matcher(noAccents).replaceAll(" ");
        String normalizedSpaces = MULTIPLE_SPACES.matcher(cleaned).replaceAll("-").trim();
        String result = MULTIPLE_HYPHENS.matcher(normalizedSpaces).replaceAll("-");
        // Loại bỏ dấu gạch ngang ở đầu và cuối chuỗi
        return result.replaceAll("^-+|-+$", "");
    }

    /**
     * Rút gọn chuỗi văn bản với độ dài tối đa và thêm dấu ba chấm (...).
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (maxLength <= 0) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        if (maxLength <= 3) {
            return text.substring(0, maxLength);
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Làm mờ địa chỉ email hiển thị công khai để bảo vệ quyền riêng tư.
     * Ví dụ: "ducnghiabachong@gmail.com" -> "d***g@gmail.com".
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email == null ? "" : email;
        }
        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (username.length() <= 2) {
            return "*".repeat(username.length()) + domain;
        }

        char firstChar = username.charAt(0);
        char lastChar = username.charAt(username.length() - 1);
        return firstChar + "***" + lastChar + domain;
    }

    /**
     * Làm mờ số điện thoại để hiển thị an toàn.
     * Ví dụ: "0912345678" -> "091***5678".
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone == null ? "" : phone;
        }
        int len = phone.length();
        String prefix = phone.substring(0, 3);
        String suffix = phone.substring(len - 4);
        return prefix + "***" + suffix;
    }

    /**
     * Thoát ký tự đặc biệt của SQL LIKE wildcard ('%', '_', '\') để tìm kiếm chính xác.
     */
    public static String escapeSqlWildcards(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * Tính khoảng cách Levenshtein giữa hai chuỗi ký tự (số thao tác chèn, xóa, thay thế tối thiểu).
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return s2.length();
        }
        if (s2 == null) {
            return s1.length();
        }

        int len1 = s1.length();
        int len2 = s2.length();

        int[] cost = new int[len2 + 1];
        for (int j = 0; j <= len2; j++) {
            cost[j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            int[] newCost = new int[len2 + 1];
            newCost[0] = i;
            for (int j = 1; j <= len2; j++) {
                int match = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                newCost[j] = Math.min(
                        Math.min(newCost[j - 1] + 1, cost[j] + 1),
                        cost[j - 1] + match
                );
            }
            cost = newCost;
        }
        return cost[len2];
    }

    /**
     * Tính tỷ lệ tương đồng giữa hai chuỗi theo độ đo Levenshtein (thang 0.0 - 1.0).
     */
    public static double similarityScore(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return (s1 == null && s2 == null) ? 1.0 : 0.0;
        }
        if (s1.equals(s2)) {
            return 1.0;
        }
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) {
            return 1.0;
        }
        int dist = levenshteinDistance(s1, s2);
        return 1.0 - ((double) dist / maxLen);
    }
}
