package lms.question;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.question.QuestionBankViews.BankDetail;
import lms.question.QuestionBankViews.BankRequest;
import lms.question.QuestionBankViews.BankSummary;
import lms.question.QuestionBankViews.DeleteQuestionsRequest;
import lms.question.QuestionBankViews.ImportReport;
import lms.question.QuestionBankViews.ImportRequest;
import lms.question.QuestionBankViews.QuestionRequest;
import lms.question.QuestionBankViews.QuestionView;
import lms.question.QuestionBankViews.Subject;

/** API giảng viên: ngân hàng câu hỏi của mình, câu hỏi bên trong, nhập câu hỏi từ văn bản. */
@RestController
@RequestMapping("/api/v1/teacher/question-banks")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherQuestionBankController {

    private final QuestionBankService service;

    public TeacherQuestionBankController(QuestionBankService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<BankSummary>> list(AuthPrincipal me) {
        return ApiResponse.ok(service.list(me));
    }

    @PostMapping
    public ApiResponse<BankSummary> create(@Valid @RequestBody BankRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.create(body, me));
    }

    /** Đường dẫn cố định "subjects" được ưu tiên hơn /{id} (public id luôn 22 ký tự). */
    @GetMapping("/subjects")
    public ApiResponse<List<Subject>> subjects(AuthPrincipal me) {
        return ApiResponse.ok(service.subjects(me));
    }

    @GetMapping("/{id}")
    public ApiResponse<BankDetail> detail(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.detail(id, me));
    }

    @PutMapping("/{id}")
    public ApiResponse<BankSummary> update(@PathVariable String id, @Valid @RequestBody BankRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(service.update(id, body, me));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id, AuthPrincipal me) {
        service.delete(id, me);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/questions")
    public ApiResponse<QuestionView> addQuestion(@PathVariable String id, @Valid @RequestBody QuestionRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(service.addQuestion(id, body, me));
    }

    /** Đường dẫn cố định "delete" được ưu tiên hơn /questions/{questionId} (public id luôn 22 ký tự). */
    @PostMapping("/{id}/questions/delete")
    public ApiResponse<Void> deleteQuestions(@PathVariable String id, @Valid @RequestBody DeleteQuestionsRequest body,
            AuthPrincipal me) {
        service.deleteQuestions(id, body.ids(), me);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/questions/{questionId}")
    public ApiResponse<QuestionView> updateQuestion(@PathVariable String id, @PathVariable String questionId,
            @Valid @RequestBody QuestionRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.updateQuestion(id, questionId, body, me));
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public ApiResponse<Void> deleteQuestion(@PathVariable String id, @PathVariable String questionId,
            AuthPrincipal me) {
        service.deleteQuestions(id, List.of(questionId), me);
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/import")
    public ApiResponse<ImportReport> importText(@PathVariable String id, @Valid @RequestBody ImportRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(service.importText(id, body.content(), me));
    }
}
