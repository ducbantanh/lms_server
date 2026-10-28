package lms.common.util;

import java.util.regex.Pattern;

/**
 * Tiện ích thẩm định dữ liệu đầu vào (Input Validation Utilities).
 * Hỗ trợ định dạng mã sinh viên Việt Nam (MSSV), email học viện, số điện thoại, dải CIDR mạng.
 */
public final class ValidationUtils {

    private static final Pattern STUDENT_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{4,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$");
    private static final Pattern VN_PHONE_PATTERN = Pattern.compile("^(0|\\+84)(3|5|7|8|9)[0-9]{8}$");
    private static final Pattern IPV4_CIDR_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(/([0-9]|[1-2][0-9]|3[0-2]))?$"
    );
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z0-9_-]{3,20}$");

    private ValidationUtils() {
    }

    /**
     * Kiểm tra định dạng mã sinh viên (MSSV).
     */
    public static boolean isValidStudentCode(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        return STUDENT_CODE_PATTERN.matcher(code.trim()).matches();
    }

    /**
     * Kiểm tra định dạng địa chỉ email chuẩn RFC.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Kiểm tra định dạng số điện thoại di động Việt Nam (10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09).
     */
    public static boolean isValidVietnamesePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return VN_PHONE_PATTERN.matcher(phone.trim().replaceAll("\\s+", "")).matches();
    }

    /**
     * Kiểm tra định dạng địa chỉ IPv4 hoặc dải IPv4 CIDR (ví dụ: 192.168.1.1 hoặc 192.168.1.0/24).
     */
    public static boolean isValidIpv4OrCidr(String cidr) {
        if (cidr == null || cidr.isBlank()) {
            return false;
        }
        return IPV4_CIDR_PATTERN.matcher(cidr.trim()).matches();
    }

    /**
     * Kiểm tra định dạng mã học phần (ví dụ: INT1339, BAS1201).
     */
    public static boolean isValidCourseCode(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return false;
        }
        return COURSE_CODE_PATTERN.matcher(courseCode.trim()).matches();
    }

    /**
     * Đánh giá độ mạnh mật khẩu (Score từ 0 đến 100).
     */
    public static int calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        int score = 0;
        int len = password.length();

        // Độ dài
        if (len >= 8) score += 25;
        if (len >= 12) score += 15;
        if (len >= 16) score += 10;

        // Chứa chữ thường
        if (password.chars().anyMatch(Character::isLowerCase)) {
            score += 15;
        }
        // Chứa chữ hoa
        if (password.chars().anyMatch(Character::isUpperCase)) {
            score += 15;
        }
        // Chứa chữ số
        if (password.chars().anyMatch(Character::isDigit)) {
            score += 10;
        }
        // Chứa ký tự đặc biệt
        if (password.chars().anyMatch(ch -> !Character.isLetterOrDigit((char) ch))) {
            score += 10;
        }

        return Math.min(100, score);
    }
}
