package lms.auth.session;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import lms.config.AppProperties;

/** Đọc / ghi / xóa cookie session (HttpOnly, SameSite, Secure khi HTTPS). */
@Component
public class SessionCookies {

    private final AppProperties.Session cfg;

    public SessionCookies(AppProperties props) {
        this.cfg = props.session();
    }

    public String name() {
        return cfg.cookieName();
    }

    public Optional<String> read(HttpServletRequest request) {
        return read(request.getCookies());
    }

    public Optional<String> read(Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie c : cookies) {
            if (cfg.cookieName().equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                return Optional.of(c.getValue());
            }
        }
        return Optional.empty();
    }

    public void write(HttpServletResponse response, String token) {
        ResponseCookie cookie = base(token).maxAge(cfg.absoluteTimeout()).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clear(HttpServletResponse response) {
        ResponseCookie cookie = base("").maxAge(0).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(cfg.cookieName(), value)
                .httpOnly(true)
                .secure(cfg.secureCookie())
                .sameSite(cfg.sameSite())
                .path("/");
    }
}
