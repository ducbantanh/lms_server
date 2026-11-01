package lms.mixed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.stream.Stream;

import lms.common.AppException;
import lms.common.ErrorCode;

/**
 * Quy tắc điểm của bài tổng hợp (Assessment.MODE_MIXED). Java thuần để dễ unit test.
 * Mỗi phần có điểm tối đa (trọng số); điểm phần = số câu được tính / tổng số câu × điểm tối đa, làm tròn 2 chữ số.
 * Trắc nghiệm tính câu đúng (QuizGrader.isCorrect), lập trình tính câu có lần nộp AC.
 */
public final class MixedGrader {

    public static final BigDecimal MAX_SCORE = BigDecimal.TEN;

    private MixedGrader() {
    }

    /** got / total × weight, làm tròn 2 chữ số; phần không có câu nào thì 0. */
    public static BigDecimal points(BigDecimal weight, int got, int total) {
        if (total <= 0 || got <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return weight.multiply(BigDecimal.valueOf(Math.min(got, total)))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    /**
     * Điểm tối đa các phần (null = không có phần đó): ít nhất hai phần, mỗi phần lớn hơn 0 và tối đa 2 chữ số thập
     * phân, tổng đúng bằng 10. Sai thì VALIDATION_FAILED.
     */
    public static void requireWeights(BigDecimal quiz, BigDecimal code, BigDecimal file) {
        var present = Stream.of(quiz, code, file).filter(Objects::nonNull).toList();
        if (present.size() < 2) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "A mixed assessment needs at least two parts");
        }
        for (BigDecimal w : present) {
            if (w.signum() <= 0 || w.stripTrailingZeros().scale() > 2) {
                throw new AppException(ErrorCode.VALIDATION_FAILED, "Invalid part weight " + w.toPlainString());
            }
        }
        BigDecimal sum = present.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(MAX_SCORE) != 0) {
            throw new AppException(ErrorCode.VALIDATION_FAILED, "Part weights must add up to 10");
        }
    }
}
