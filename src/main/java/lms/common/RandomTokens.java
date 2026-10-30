package lms.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Sinh token ngẫu nhiên và băm token. Java thuần, không phụ thuộc Spring.
 */
public final class RandomTokens {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    private RandomTokens() {
    }

    /** Token base64url từ {@code bytes} byte ngẫu nhiên (32 byte -> 43 ký tự). */
    public static String urlSafe(int bytes) {
        byte[] buf = new byte[bytes];
        RANDOM.nextBytes(buf);
        return B64.encodeToString(buf);
    }

    /** Session token: 256 bit. */
    public static String sessionToken() {
        return urlSafe(32);
    }

    /** CSRF token: 256 bit. */
    public static String csrfToken() {
        return urlSafe(32);
    }

    /** Public ID cho user/resource: 128 bit -> 22 ký tự. */
    public static String publicId() {
        return urlSafe(16);
    }

    /** Mã số ngẫu nhiên có đủ số chữ số, dùng cho OTP gửi qua kênh ngoài. */
    public static String numericCode(int digits) {
        if (digits < 1 || digits > 9) {
            throw new IllegalArgumentException("digits must be between 1 and 9");
        }
        int bound = 1;
        for (int i = 0; i < digits; i++) {
            bound *= 10;
        }
        return String.format(Locale.ROOT, "%0" + digits + "d", RANDOM.nextInt(bound));
    }

    /** SHA-256 hex (64 ký tự). Dùng để lưu session token trong DB. */
    public static String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /** So sánh chuỗi thời gian hằng, tránh timing attack. */
    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
