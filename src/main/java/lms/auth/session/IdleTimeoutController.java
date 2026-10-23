package lms.auth.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.RequestInfo;

/** Trang Cài đặt của admin: thời gian tự đăng xuất khi không hoạt động của sinh viên và giảng viên. */
@RestController
@RequestMapping("/api/v1/admin/settings/idle-timeout")
@RequirePermission(Permissions.SESSIONS_MANAGE)
public class IdleTimeoutController {

    private final IdleTimeoutService service;

    public IdleTimeoutController(IdleTimeoutService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<IdleTimeoutService.Settings> get() {
        return ApiResponse.ok(service.settings());
    }

    @PutMapping
    public ApiResponse<IdleTimeoutService.Settings> update(@Valid @RequestBody IdleTimeoutService.UpdateRequest body,
            AuthPrincipal me, HttpServletRequest req) {
        return ApiResponse.ok(service.update(body, me, RequestInfo.clientIp(req)));
    }
}
