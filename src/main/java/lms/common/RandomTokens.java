package lms.common;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Tiện ích sinh mã ngẫu nhiên bảo mật (Cryptographically Secure Random Tokens)
 * và các giải thuật băm một chiều phục vụ xác thực, phiên làm việc, chống giả mạo CSRF và chống Timing Attacks.
 * Thuần Java (Không phụ thuộc Spring Framework).
 */
public final class RandomTokens {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    private static final char[] ALPHANUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] ALPHANUMERIC_UPPER_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] NUMERIC_DIGITS = "0123456789".toCharArray();

    private RandomTokens() {
    }

    /**
     * Token base64url từ {@code bytes} byte ngẫu nhiên (32 byte -> 43 ký tự an toàn trên URL).
     */
    public static String urlSafe(int bytes) {
        if (bytes <= 0) {
            throw new IllegalArgumentException("Số byte ngẫu nhiên phải lớn hơn 0");
        }
        byte[] buf = new byte[bytes];
        RANDOM.nextBytes(buf);
        return B64.encodeToString(buf);
    }

    /** Session token: 256 bit (32 byte). */
    public static String sessionToken() {
        return urlSafe(32);
    }

    /** CSRF token: 256 bit (32 byte). */
    public static String csrfToken() {
        return urlSafe(32);
    }

    /** Public ID cho user/resource: 128 bit -> 22 ký tự. */
    public static String publicId() {
        return urlSafe(16);
    }

    /**
     * Sinh mã số PIN ngẫu nhiên (chỉ gồm chữ số 0-9) với độ dài chỉ định.
     */
    public static String numericPin(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Độ dài mã PIN phải lớn hơn 0");
        }
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = NUMERIC_DIGITS[RANDOM.nextInt(NUMERIC_DIGITS.length)];
        }
        return new String(result);
    }

    /**
     * Sinh chuỗi ngẫu nhiên gồm chữ và số (Alphanumeric).
     */
    public static String alphanumeric(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Độ dài chuỗi ngẫu nhiên phải lớn hơn 0");
        }
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = ALPHANUMERIC_CHARS[RANDOM.nextInt(ALPHANUMERIC_CHARS.length)];
        }
        return new String(result);
    }

    /**
     * Sinh chuỗi ngẫu nhiên chữ hoa và số (ví dụ mã tham gia khóa học ENROLL_KEY).
     */
    public static String alphanumericUpper(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Độ dài chuỗi ngẫu nhiên phải lớn hơn 0");
        }
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = ALPHANUMERIC_UPPER_CHARS[RANDOM.nextInt(ALPHANUMERIC_UPPER_CHARS.length)];
        }
        return new String(result);
    }

    /**
     * Sinh UUID V4 dạng chuẩn xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx.
     */
    public static String uuidString() {
        return UUID.randomUUID().toString();
    }

    /**
     * SHA-256 hex (64 ký tự). Dùng để băm lưu token session hoặc checksum trong DB.
     */
    public static String sha256Hex(String value) {
        if (value == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Thuật toán SHA-256 không khả dụng trên JVM này", e);
        }
    }

    /**
     * Tính toán chữ ký HMAC-SHA256 phục vụ xác thực tin nhắn hoặc webhook.
     */
    public static String hmacSha256Hex(String data, String secretKey) {
        if (data == null || secretKey == null) {
            throw new IllegalArgumentException("Data và Secret Key không được phép null");
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Lỗi khi tính HMAC-SHA256", e);
        }
    }

    /**
     * So sánh chuỗi thời gian hằng (Constant-time comparison) để phòng tránh Timing Attack.
     */
    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
