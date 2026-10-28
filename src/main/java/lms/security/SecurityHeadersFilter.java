package lms.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter thiết lập các Header bảo mật HTTP chuẩn OWASP (HTTP Security Headers).
 * Bảo vệ ứng dụng khỏi các nguy cơ Clickjacking, MIME-sniffing, XSS và rò rỉ Referrer.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Chống MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Chống nhúng iframe từ trang web độc hại bên ngoài (Clickjacking)
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        // Kích hoạt bộ lọc XSS của trình duyệt
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Giới hạn thông tin Referrer khi gửi yêu cầu sang domain khác
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Giới hạn quyền truy cập phần cứng nhạy cảm
        response.setHeader("Permissions-Policy", "camera=(self), microphone=(), geolocation=()");

        // Cache control nghiêm ngặt cho toàn bộ các endpoint nghiệp vụ API
        if (request.getRequestURI().startsWith("/api/")) {
            response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }

        filterChain.doFilter(request, response);
    }
}
