package lms.realtime;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lms.auth.AuthPrincipal;
import lms.auth.session.SessionCookies;
import lms.auth.session.SessionService;

/**
 * Xác thực cookie session TRƯỚC khi nâng cấp lên WebSocket.
 * Không có session hợp lệ -> trả 401, không mở socket.
 */
@Component
public class SessionHandshakeInterceptor implements HandshakeInterceptor {

    public static final String PRINCIPAL_ATTR = "principal";

    private final SessionCookies cookies;
    private final SessionService sessions;

    public SessionHandshakeInterceptor(SessionCookies cookies, SessionService sessions) {
        this.cookies = cookies;
        this.sessions = sessions;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }
        Optional<AuthPrincipal> principal = cookies.read(servletRequest.getServletRequest())
                .flatMap(sessions::authenticate);
        if (principal.isEmpty()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        attributes.put(PRINCIPAL_ATTR, principal.get());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        // không cần xử lý
    }
}
