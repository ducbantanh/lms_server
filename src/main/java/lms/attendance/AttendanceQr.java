package lms.attendance;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lms.common.RandomTokens;

/**
 * Mã QR điểm danh, đổi mỗi {@link #PERIOD_SECONDS} giây mà không cần lưu gì vào DB:
 * {@code <id buổi>.<số chu kỳ base36>.<HMAC-SHA256(khóa của buổi, id buổi + "." + chu kỳ), 16 byte base64url>}.
 * Chụp ảnh mã gửi cho bạn ở nơi khác chỉ dùng được trong chu kỳ hiện tại và chu kỳ ngay trước đó
 * (tối đa 2 chu kỳ = 20 giây), đủ cho người quét lúc mã vừa đổi. Java thuần để test không cần Spring.
 */
public final class AttendanceQr {

    public static final int PERIOD_SECONDS = 10;
    private static final long PERIOD_MS = PERIOD_SECONDS * 1000L;
    /** Số chu kỳ cũ vẫn nhận (ngoài chu kỳ hiện tại). */
    private static final int GRACE_PERIODS = 1;
    private static final int SIGNATURE_BYTES = 16;
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    /** Mã đã tách: id công khai của buổi, chu kỳ, chữ ký. */
    public record Parsed(String sessionId, long period, String signature) {
    }

    private AttendanceQr() {
    }

    public static long period(Instant now) {
        return Math.floorDiv(now.toEpochMilli(), PERIOD_MS);
    }

    /** Thời điểm mã của chu kỳ này hết lượt hiển thị (giảng viên lấy mã mới). */
    public static Instant periodEnd(long period) {
        return Instant.ofEpochMilli((period + 1) * PERIOD_MS);
    }

    public static String token(String sessionId, String secret, long period) {
        return sessionId + "." + Long.toString(period, 36) + "." + sign(sessionId, secret, period);
    }

    /** Tách mã; empty khi sai định dạng (không phải mã của hệ thống). */
    public static Optional<Parsed> parse(String token) {
        if (token == null) {
            return Optional.empty();
        }
        String[] parts = token.trim().split("\\.");
        if (parts.length != 3 || !parts[0].matches("[A-Za-z0-9_-]{22}") || !parts[1].matches("[0-9a-z]{1,12}")
                || !parts[2].matches("[A-Za-z0-9_-]{22}")) {
            return Optional.empty();
        }
        return Optional.of(new Parsed(parts[0], Long.parseLong(parts[1], 36), parts[2]));
    }

    /** Đúng chữ ký và thuộc chu kỳ hiện tại hoặc chu kỳ ân hạn ngay trước. */
    public static boolean isValid(Parsed parsed, String secret, long currentPeriod) {
        long age = currentPeriod - parsed.period();
        return age >= 0 && age <= GRACE_PERIODS
                && RandomTokens.constantTimeEquals(parsed.signature(), sign(parsed.sessionId(), secret, parsed.period()));
    }

    private static String sign(String sessionId, String secret, long period) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal((sessionId + "." + period).getBytes(StandardCharsets.UTF_8));
            return B64.encodeToString(Arrays.copyOf(digest, SIGNATURE_BYTES));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HmacSHA256 not available", e);
        }
    }
}
