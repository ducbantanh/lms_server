package lms.quiz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Chấm và xáo đề bài trắc nghiệm. Java thuần để dễ unit test.
 * Một câu đúng khi tập đáp án chọn trùng khít tập đáp án đúng (câu nhiều đáp án phải chọn đủ, không thừa);
 * điểm = số câu đúng / tổng số câu × 10, làm tròn 2 chữ số.
 */
public final class QuizGrader {

    public static final BigDecimal MAX_SCORE = BigDecimal.TEN;

    private QuizGrader() {
    }

    public record Result(int correct, int total, BigDecimal score) {
    }

    /** Vị trí các đáp án đúng của câu. */
    public static Set<Integer> correctPositions(QuizQuestion q) {
        List<QuizOption> options = q.getOptions();
        return IntStream.range(0, options.size()).filter(i -> options.get(i).isCorrect()).boxed()
                .collect(Collectors.toSet());
    }

    public static boolean isCorrect(QuizQuestion q, Set<Integer> chosen) {
        return chosen != null && !chosen.isEmpty() && chosen.equals(correctPositions(q));
    }

    /** answers: id câu hỏi → vị trí các đáp án đã chọn (câu chưa trả lời thì không có trong map). */
    public static Result grade(List<QuizQuestion> questions, Map<Long, Set<Integer>> answers) {
        int correct = (int) questions.stream().filter(q -> isCorrect(q, answers.get(q.getId()))).count();
        int total = questions.size();
        BigDecimal score = total == 0 ? BigDecimal.ZERO.setScale(2)
                : MAX_SCORE.multiply(BigDecimal.valueOf(correct)).divide(BigDecimal.valueOf(total), 2,
                        RoundingMode.HALF_UP);
        return new Result(correct, total, score);
    }

    /**
     * Thứ tự xáo cố định theo seed: mỗi sinh viên một thứ tự riêng, tải lại trang vẫn giữ nguyên.
     * java.util.Random có thuật toán cố định trong đặc tả Java nên kết quả không đổi giữa các lần chạy.
     */
    public static <T> List<T> shuffled(List<T> list, long seed) {
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy, new Random(seed));
        return copy;
    }

    /** Seed cho một sinh viên (theo bài nộp) và một phần của đề (salt: 0 thứ tự câu, id câu cho thứ tự đáp án). */
    public static long seed(String submissionPublicId, long salt) {
        return submissionPublicId.hashCode() * 31L + salt;
    }
}
