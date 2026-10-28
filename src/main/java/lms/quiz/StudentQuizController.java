package lms.quiz;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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
import lms.common.RequestInfo;
import lms.quiz.QuizViews.AnswerRequest;
import lms.quiz.QuizViews.AnswerSaved;
import lms.quiz.QuizViews.QuizState;

/** API sinh viên làm bài trắc nghiệm của khóa học đã ghi danh. */
@RestController
@RequestMapping("/api/v1/student/assessments/{id}/quiz")
@RequirePermission(Permissions.SUBMISSIONS_CREATE)
public class StudentQuizController {

    private final QuizService service;

    public StudentQuizController(QuizService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<QuizState> state(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.state(id, me));
    }

    @PostMapping("/start")
    public ApiResponse<QuizState> start(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.start(id, me));
    }

    @PutMapping("/answers/{questionId}")
    public ApiResponse<AnswerSaved> answer(@PathVariable String id, @PathVariable String questionId,
            @Valid @RequestBody AnswerRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.answer(id, questionId, body.chosen(), me));
    }

    @PostMapping("/submit")
    public ApiResponse<QuizState> submit(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.submit(id, me, RequestInfo.clientIp(req)));
    }
}
