package lms.auth;

import java.util.Optional;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import lms.auth.session.SessionCookies;
import lms.auth.session.SessionService;
import lms.common.AppException;
import lms.common.ErrorCode;
import lms.common.RandomTokens;

/**
 * Chạy trước mọi /api/**:
 * 1. Đọc cookie session, xác thực với DB.
 * 2. Endpoint không có @PublicEndpoint thì bắt buộc đăng nhập.
 * 3. Request thay đổi dữ liệu (POST/PUT/PATCH/DELETE) phải có header X-CSRF-Token khớp session.
 * 4. User có must_change_password = 1 chỉ được gọi /api/v1/auth/** cho tới khi đổi mật khẩu.
 * 5. Kiểm tra @RequirePermission ở class và method.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String CSRF_HEADER = "X-CSRF-Token";
    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    private static final String AUTH_PATH_PREFIX = "/api/v1/auth/";

    private final SessionService sessionService;
    private final SessionCookies cookies;
    private final PermissionService permissionService;

    public AuthInterceptor(SessionService sessionService, SessionCookies cookies, PermissionService permissionService) {
        this.sessionService = sessionService;
        this.cookies = cookies;
        this.permissionService = permissionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        Optional<AuthPrincipal> principal = cookies.read(request).flatMap(sessionService::authenticate);
        principal.ifPresent(p -> request.setAttribute(AuthPrincipal.REQUEST_ATTRIBUTE, p));

        if (isPublic(method)) {
            return true;
        }

        AuthPrincipal p = principal.orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!SAFE_METHODS.contains(request.getMethod())
                && !RandomTokens.constantTimeEquals(request.getHeader(CSRF_HEADER), p.csrfToken())) {
            throw new AppException(ErrorCode.CSRF_INVALID);
        }

        // Tài khoản import có mật khẩu = ngày sinh (dễ đoán) -> bắt đổi trước khi dùng API khác
        if (p.mustChangePassword() && !request.getRequestURI().startsWith(AUTH_PATH_PREFIX)) {
            throw new AppException(ErrorCode.PASSWORD_CHANGE_REQUIRED);
        }

        checkPermissions(AnnotatedElementUtils.findMergedAnnotation(method.getBeanType(), RequirePermission.class), p);
        checkPermissions(method.getMethodAnnotation(RequirePermission.class), p);
        return true;
    }

    private boolean isPublic(HandlerMethod method) {
        return method.hasMethodAnnotation(PublicEndpoint.class)
                || AnnotatedElementUtils.hasAnnotation(method.getBeanType(), PublicEndpoint.class);
    }

    private void checkPermissions(RequirePermission required, AuthPrincipal p) {
        if (required == null) {
            return;
        }
        for (String perm : required.value()) {
            if (!permissionService.has(p.role(), perm)) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }
        }
    }
}
