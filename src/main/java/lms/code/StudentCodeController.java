package lms.code;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.code.CodeViews.AttemptView;
import lms.code.CodeViews.CodeState;
import lms.code.CodeViews.ProblemDetail;
import lms.code.CodeViews.RunRequest;
import lms.common.ApiResponse;
import lms.common.RequestInfo;

/** API sinh viên làm bài lập trình (nhiều câu) của khóa học đã ghi danh. */
@RestController
@RequestMapping("/api/v1/student/assessments/{id}/code")
@RequirePermission(Permissions.SUBMISSIONS_CREATE)
public class StudentCodeController {

    private final CodeService service;

    public StudentCodeController(CodeService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<CodeState> state(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.state(id, me));
    }

    @GetMapping("/problems/{problemId}")
    public ApiResponse<ProblemDetail> problem(@PathVariable String id, @PathVariable String problemId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.problem(id, problemId, me));
    }

    @PostMapping("/problems/{problemId}/run")
    public ApiResponse<AttemptView> run(@PathVariable String id, @PathVariable String problemId,
            @Valid @RequestBody RunRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.run(id, problemId, body, me));
    }

    @PostMapping("/problems/{problemId}/submit")
    public ApiResponse<AttemptView> submit(@PathVariable String id, @PathVariable String problemId,
            @Valid @RequestBody RunRequest body, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.submit(id, problemId, body, me, RequestInfo.clientIp(req)));
    }

    @GetMapping("/attempts/{attemptId}")
    public ApiResponse<AttemptView> attempt(@PathVariable String id, @PathVariable String attemptId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.attempt(id, attemptId, me));
    }
}
