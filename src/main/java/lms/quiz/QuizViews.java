package lms.quiz;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO bài trắc nghiệm, khớp lms_client/lib/quiz.ts; mọi id trả ra ngoài là public_id. */
public final class QuizViews {

    private QuizViews() {
    }

    /** Giai đoạn làm bài của sinh viên. */
    public static final String NOT_STARTED = "not_started";
    public static final String IN_PROGRESS = "in_progress";
    public static final String FINISHED = "finished";

    // ---------------------------------------------------------------- response

    /** correct null = chưa được xem đáp án. */
    public record OptionView(int position, String body, Boolean correct) {
    }

    /**
     * Một câu theo thứ tự hiển thị (đã xáo nếu bài bật xáo trộn); chosen = vị trí các đáp án đã chọn.
     * correct, explanation null = chưa được xem đáp án.
     */
    public record QuestionView(String id, String body, boolean multiple, List<OptionView> options,
            List<Integer> chosen, Boolean correct, String explanation) {
    }

    public record ResultView(int correct, int total, BigDecimal score) {
    }

    /**
     * Bài trắc nghiệm của sinh viên. questions chỉ có khi đang làm, hoặc đã xong và được xem đáp án;
     * deadline null = không hạn; result chỉ có khi đã xong và điểm đã công bố (score là điểm chính thức,
     * giảng viên có thể đã sửa). scoreHidden: đã xong nhưng điểm chưa công bố (sau giờ đóng, hoặc không công bố).
     */
    public record QuizState(String phase, int questionCount, Integer timeLimitMinutes, Instant startedAt,
            Instant deadline, Instant submittedAt, boolean revealed, List<QuestionView> questions, ResultView result,
            boolean scoreHidden, Instant serverTime) {
    }

    /** Thống kê một câu trên các bài đã chốt: số người trả lời, số người đúng. */
    public record QuestionStat(String questionId, int answered, int correct) {
    }

    /** Giảng viên xem đề của bài (luôn kèm đáp án) và thống kê từng câu. finished = số bài đã chốt. */
    public record TeacherQuiz(List<QuestionView> questions, List<QuestionStat> stats, int finished) {
    }

    /** Bài làm của một sinh viên cho giảng viên xem (kèm đáp án đúng); result null khi chưa chốt. */
    public record StudentReview(String studentId, String username, String fullName, Instant startedAt,
            Instant submittedAt, ResultView result, List<QuestionView> questions) {
    }

    public record AnswerSaved(String questionId, List<Integer> chosen, Instant savedAt) {
    }

    // ---------------------------------------------------------------- request

    /**
     * Tạo bài trắc nghiệm từ câu hỏi trong ngân hàng của mình (questionIds theo thứ tự trong đề).
     * type: Assessment.TYPE_LAB hoặc TYPE_EXAM; revealMode: Assessment.REVEAL_*.
     */
    public record CreateQuizRequest(
            @Min(1) @Max(2) int type,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            Instant opensAt,
            Instant closesAt,
            @Min(1) @Max(600) Integer timeLimitMinutes,
            boolean shuffle,
            @Min(0) @Max(2) int revealMode,
            /** Assessment.SCORE_*; sau giờ đóng thì cần closesAt. */
            @Min(0) @Max(2) int scoreRelease,
            @NotEmpty @Size(max = QuizService.MAX_QUESTIONS) List<@NotBlank String> questionIds) {
    }

    /** Vị trí các đáp án chọn (0 = A); rỗng = bỏ chọn câu này. */
    public record AnswerRequest(@NotNull @Size(max = 10) List<@NotNull Integer> chosen) {
    }
}
