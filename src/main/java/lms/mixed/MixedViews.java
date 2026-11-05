package lms.mixed;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lms.code.CodeViews.CodeRequest;
import lms.quiz.QuizService;

/** DTO bài tổng hợp, khớp lms_client/lib/mixed.ts; mọi id trả ra ngoài là public_id. */
public final class MixedViews {

    private MixedViews() {
    }

    // ---------------------------------------------------------------- response

    /** Phần trắc nghiệm; correct, points chỉ có khi đã nộp và điểm đã công bố. */
    public record QuizPartView(BigDecimal weight, int questionCount, Integer correct, BigDecimal points) {
    }

    /** Phần lập trình; accepted (số câu AC), points chỉ có khi đã nộp và điểm đã công bố. pending: còn lần nộp chờ chấm. */
    public record CodePartView(BigDecimal weight, int problemCount, Integer accepted, BigDecimal points,
            boolean pending) {
    }

    /** Phần nộp file; points là điểm giảng viên chấm, chỉ có khi đã chấm và điểm đã công bố. */
    public record FilePartView(BigDecimal weight, int fileCount, BigDecimal points) {
    }

    /**
     * Lượt làm bài tổng hợp của sinh viên. phase như QuizViews (not_started / in_progress / finished); deadline null =
     * không hạn; phần không có trong bài thì null. score, feedback chỉ có khi đã nộp và điểm đã công bố (score null
     * khi phần nộp file chưa chấm). scoreHidden: đã nộp nhưng điểm chưa công bố.
     */
    public record MixedState(String phase, Integer timeLimitMinutes, Instant startedAt, Instant deadline,
            Instant submittedAt, QuizPartView quiz, CodePartView code, FilePartView file, BigDecimal score,
            String feedback, boolean scoreHidden, Instant serverTime) {
    }

    // ---------------------------------------------------------------- request

    /** Phần trắc nghiệm: câu hỏi trong ngân hàng của mình theo thứ tự trong đề. */
    public record QuizPartRequest(
            @NotNull @DecimalMin("0.01") @DecimalMax("10") @Digits(integer = 2, fraction = 2) BigDecimal weight,
            @NotEmpty @Size(max = QuizService.MAX_QUESTIONS) List<@NotBlank String> questionIds) {
    }

    /** Phần lập trình: ngôn ngữ, các câu và bộ test như bài lập trình (file ZIP bộ test là part multipart). */
    public record CodePartRequest(
            @NotNull @DecimalMin("0.01") @DecimalMax("10") @Digits(integer = 2, fraction = 2) BigDecimal weight,
            @NotNull @Valid CodeRequest code) {
    }

    /** Phần nộp file: đề PDF (không bắt buộc) là part "paper" của request multipart. */
    public record FilePartRequest(
            @NotNull @DecimalMin("0.01") @DecimalMax("10") @Digits(integer = 2, fraction = 2) BigDecimal weight) {
    }

    /**
     * Tạo bài tổng hợp: ít nhất hai phần, điểm tối đa các phần cộng lại bằng 10 (MixedGrader.requireWeights).
     * Bài tập (type 0) không có phần trắc nghiệm, như bài trắc nghiệm. timeLimitMinutes, shuffle, revealMode như bài
     * trắc nghiệm và tính chung cho cả bài; notifyStudents chỉ có tác dụng với bài tập.
     */
    public record CreateMixedRequest(
            @Min(0) @Max(2) int type,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            Instant opensAt,
            Instant closesAt,
            @Min(1) @Max(600) Integer timeLimitMinutes,
            boolean shuffle,
            @Min(0) @Max(2) int revealMode,
            @Min(0) @Max(2) int scoreRelease,
            boolean notifyStudents,
            @Valid QuizPartRequest quiz,
            @Valid CodePartRequest code,
            @Valid FilePartRequest file) {
    }
}
