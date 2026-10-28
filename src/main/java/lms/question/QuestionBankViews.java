package lms.question;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO ngân hàng câu hỏi, khớp lms_client/lib/question-bank.ts; mọi id trả ra ngoài là public_id. */
public final class QuestionBankViews {

    private QuestionBankViews() {
    }

    // ---------------------------------------------------------------- response

    /**
     * subjectCode/subjectName: môn học (mã học phần) của ngân hàng, null = chưa gắn môn.
     * updatedAt: lần sửa ngân hàng hoặc câu hỏi bên trong gần nhất.
     */
    public record BankSummary(String id, String name, String description, String subjectCode, String subjectName,
            long questionCount, Instant createdAt, Instant updatedAt) {
    }

    /** Môn học chọn được cho ngân hàng: học phần của các khóa học mình phụ trách. */
    public record Subject(String code, String name) {
    }

    public record OptionView(String body, boolean correct) {
    }

    /** difficulty theo BankQuestion.DIFFICULTY_*; options theo thứ tự A, B, C... */
    public record QuestionView(String id, String body, boolean multiple, int difficulty, String explanation,
            List<OptionView> options, Instant createdAt, Instant updatedAt) {
    }

    /** Câu hỏi theo thứ tự thêm vào. */
    public record BankDetail(BankSummary bank, List<QuestionView> questions) {
    }

    /** Có lỗi thì không thêm câu nào (added = 0): sửa văn bản rồi dán lại, không bị trùng câu đã thêm. */
    public record ImportReport(int added, List<QuestionImportParser.LineIssue> errors) {
    }

    // ---------------------------------------------------------------- request

    /** subjectCode: mã học phần trong GET /subjects (giữ nguyên môn đang gắn thì được cả khi không còn dạy); trống = không gắn. */
    public record BankRequest(
            @NotBlank @Size(max = 200) String name,
            @Size(max = 2000) String description,
            @Size(max = 32) String subjectCode) {
    }

    public record OptionRequest(
            @NotBlank @Size(max = QuestionImportParser.MAX_OPTION) String body,
            boolean correct) {
    }

    /** Câu chọn một cần đúng một đáp án đúng; câu chọn nhiều cần ít nhất một. */
    public record QuestionRequest(
            @NotBlank @Size(max = QuestionImportParser.MAX_BODY) String body,
            boolean multiple,
            @Min(0) @Max(2) int difficulty,
            @Size(max = QuestionImportParser.MAX_EXPLANATION) String explanation,
            @NotNull @Size(min = QuestionImportParser.MIN_OPTIONS,
                    max = QuestionImportParser.MAX_OPTIONS) List<@Valid @NotNull OptionRequest> options) {
    }

    /** Văn bản dán vào theo định dạng của QuestionImportParser. */
    public record ImportRequest(@NotNull @Size(max = 500_000) String content) {
    }

    public record DeleteQuestionsRequest(@NotEmpty @Size(max = QuestionBankService.MAX_QUESTIONS) List<String> ids) {
    }
}
