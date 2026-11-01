package lms.mixed;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.RequestInfo;
import lms.mixed.MixedViews.MixedState;

/**
 * API sinh viên làm bài tổng hợp: trạng thái, bắt đầu, nộp cả bài. Câu trả lời từng phần gửi qua API của phần đó
 * (/quiz/answers, /code/problems, /files); bài có giám sát thì mọi API này phải qua phiên thi (ProctorInterceptor).
 */
@RestController
@RequestMapping("/api/v1/student/assessments/{id}/mixed")
@RequirePermission(Permissions.SUBMISSIONS_CREATE)
public class StudentMixedController {

    private final MixedService service;

    public StudentMixedController(MixedService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<MixedState> state(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.state(id, me));
    }

    @PostMapping("/start")
    public ApiResponse<MixedState> start(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(service.start(id, me));
    }

    @PostMapping("/submit")
    public ApiResponse<MixedState> submit(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.submit(id, me, RequestInfo.clientIp(req)));
    }
}
