package lms.proctor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lms.common.RandomTokens;

/**
 * Mã phòng thi 6 chữ số, đổi mỗi {@link #PERIOD_SECONDS} giây, không lưu DB: HMAC-SHA256(khóa của bài, id bài +
 * "." + chu kỳ) cắt còn 6 chữ số. Giám thị chiếu hoặc đọc mã trong phòng; người ở ngoài phòng không biết mã nên
 * không vào thi được. Nhận mã của chu kỳ hiện tại và chu kỳ ngay trước (người gõ lúc mã vừa đổi).
 * Java thuần để test không cần Spring.
 */
public final class ProctorCode {

    public static final int PERIOD_SECONDS = 60;
    private static final long PERIOD_MS = PERIOD_SECONDS * 1000L;
    private static final int GRACE_PERIODS = 1;
    private static final int MODULO = 1_000_000;

    private ProctorCode() {
    }

    public static long period(Instant now) {
        return Math.floorDiv(now.toEpochMilli(), PERIOD_MS);
    }

    /** Lúc mã của chu kỳ này thôi hiển thị (màn giám thị lấy mã mới). */
    public static Instant periodEnd(long period) {
        return Instant.ofEpochMilli((period + 1) * PERIOD_MS);
    }

    public static String code(String assessmentId, String secret, long period) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal((assessmentId + "." + period).getBytes(StandardCharsets.UTF_8));
            int value = ByteBuffer.wrap(digest, 0, 4).getInt() & 0x7FFFFFFF;
            return String.format("%06d", value % MODULO);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HmacSHA256 not available", e);
        }
    }

    /** Mã sinh viên gõ (bỏ khoảng trắng) khớp chu kỳ hiện tại hoặc chu kỳ ân hạn ngay trước. */
    public static boolean isValid(String input, String assessmentId, String secret, long currentPeriod) {
        if (input == null || secret == null) {
            return false;
        }
        String typed = input.replaceAll("\\s", "");
        if (!typed.matches("\\d{6}")) {
            return false;
        }
        for (int back = 0; back <= GRACE_PERIODS; back++) {
            if (RandomTokens.constantTimeEquals(typed, code(assessmentId, secret, currentPeriod - back))) {
                return true;
            }
        }
        return false;
    }
}
