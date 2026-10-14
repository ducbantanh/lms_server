package lms.realtime;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lms.auth.AuthPrincipal;

/**
 * Endpoint /ws. Server là bên phát sự kiện chính; client hiện chỉ gửi {"type":"ping"}.
 * Mọi thao tác thay đổi dữ liệu vẫn đi qua HTTP API (có CSRF + permission), không qua socket.
 */
@Component
public class RealtimeWebSocketHandler extends TextWebSocketHandler {

    private final RealtimeHub hub;
    private final ObjectMapper mapper;

    public RealtimeWebSocketHandler(RealtimeHub hub, ObjectMapper mapper) {
        this.hub = hub;
        this.mapper = mapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object p = session.getAttributes().get(SessionHandshakeInterceptor.PRINCIPAL_ATTR);
        if (!(p instanceof AuthPrincipal principal)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        hub.register(session, principal);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String type;
        try {
            JsonNode node = mapper.readTree(message.getPayload());
            type = node.path("type").asText("");
        } catch (Exception e) {
            return; // bỏ qua message sai định dạng
        }
        if ("ping".equals(type)) {
            hub.find(session.getId()).ifPresent(c -> hub.send(c, RealtimeEvent.of(RealtimeEvent.PONG, null)));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        hub.unregister(session.getId());
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        hub.unregister(session.getId());
    }
}
