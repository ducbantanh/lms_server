package lms.proctor;

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
import lms.common.ApiResponse;
import lms.common.RequestInfo;
import lms.proctor.ProctorViews.ReportRequest;
import lms.proctor.ProctorViews.StartRequest;
import lms.proctor.ProctorViews.StudentProctor;

/** Sinh viên: vào phòng thi, gửi nhịp và sự kiện của trang làm bài, kết thúc bài thi lập trình. */
@RestController
@RequestMapping("/api/v1/student/assessments/{id}/proctor")
@RequirePermission(Permissions.SUBMISSIONS_CREATE)
public class StudentProctorController {

    private final ProctorService service;

    public StudentProctorController(ProctorService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<StudentProctor> state(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.studentState(id, me, RequestInfo.clientIp(req)));
    }

    @PostMapping("/start")
    public ApiResponse<StudentProctor> start(@PathVariable String id, @Valid @RequestBody StartRequest body,
            AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.start(id, body.accessCode(), me, RequestInfo.clientIp(req),
                RequestInfo.userAgent(req)));
    }

    @PostMapping("/events")
    public ApiResponse<StudentProctor> report(@PathVariable String id, @Valid @RequestBody ReportRequest body,
            AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.report(id, body, me, RequestInfo.clientIp(req)));
    }

    @PostMapping("/finish")
    public ApiResponse<StudentProctor> finish(@PathVariable String id, AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.finish(id, me, RequestInfo.clientIp(req)));
    }
}
