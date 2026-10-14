package lms.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthDtos.ChangePasswordRequest;
import lms.auth.AuthDtos.LoginRequest;
import lms.auth.AuthDtos.SessionView;
import lms.auth.session.SessionCookies;
import lms.common.ApiResponse;
import lms.common.RequestInfo;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionCookies cookies;

    public AuthController(AuthService authService, SessionCookies cookies) {
        this.authService = authService;
        this.cookies = cookies;
    }

    @PublicEndpoint
    @PostMapping("/login")
    public ApiResponse<SessionView> login(@Valid @RequestBody LoginRequest body,
            HttpServletRequest request, HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(
                body.username(), body.password(),
                RequestInfo.clientIp(request), RequestInfo.userAgent(request));
        cookies.write(response, result.rawToken());
        return ApiResponse.ok(result.session());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(AuthPrincipal me, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(me, RequestInfo.clientIp(request));
        cookies.clear(response);
        return ApiResponse.ok();
    }

    /** Frontend gọi khi load trang để biết đã đăng nhập chưa và lấy lại csrfToken. */
    @GetMapping("/me")
    public ApiResponse<SessionView> me(AuthPrincipal me) {
        return ApiResponse.ok(authService.toSessionView(me));
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(AuthPrincipal me, @Valid @RequestBody ChangePasswordRequest body,
            HttpServletRequest request) {
        authService.changePassword(me, body.currentPassword(), body.newPassword(), RequestInfo.clientIp(request));
        return ApiResponse.ok();
    }
}
