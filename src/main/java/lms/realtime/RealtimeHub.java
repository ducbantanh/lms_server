package lms.realtime;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lms.auth.AuthPrincipal;
import lms.auth.PermissionService;
import lms.auth.Permissions;

/**
 * Sổ đăng ký mọi kết nối WebSocket đang mở, gắn với user + session.
 *
 * Presence: một user "online" khi có ÍT NHẤT một socket đang mở (không dựa vào session còn hạn).
 * Chỉ phát presence.changed khi user chuyển 0 -> 1 socket (online) hoặc 1 -> 0 (offline).
 */
@Component
public class RealtimeHub {

    /** Mã đóng riêng của app (4000-4999). Client thấy mã này thì KHÔNG tự reconnect. */
    public static final int CLOSE_SESSION_REVOKED = 4001;

    private static final Logger log = LoggerFactory.getLogger(RealtimeHub.class);
    private static final int SEND_TIME_LIMIT_MS = 5_000;
    private static final int SEND_BUFFER_LIMIT = 64 * 1024;

    public record Connection(String id, WebSocketSession ws, AuthPrincipal principal, Instant connectedAt) {
    }

    public record OnlineUser(String id, String username, String fullName, int role, int connections,
            Instant since) {
    }

    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    private final Map<Long, Integer> socketsPerUser = new ConcurrentHashMap<>();

    private final ObjectMapper mapper;
    private final PermissionService permissions;

    public RealtimeHub(ObjectMapper mapper, PermissionService permissions) {
        this.mapper = mapper;
        this.permissions = permissions;
    }

    // ------------------------------------------------------------------ vòng đời kết nối

    public void register(WebSocketSession raw, AuthPrincipal p) {
        WebSocketSession ws = new ConcurrentWebSocketSessionDecorator(raw, SEND_TIME_LIMIT_MS, SEND_BUFFER_LIMIT);
        Connection conn = new Connection(raw.getId(), ws, p, Instant.now());
        connections.put(conn.id(), conn);
        int count = socketsPerUser.merge(p.userId(), 1, Integer::sum);

        send(conn, RealtimeEvent.of(RealtimeEvent.CONNECTED, Map.of("userId", p.publicId())));
        if (count == 1) {
            broadcastPresence(p, true, 1);
        }
        log.debug("WS open user={} session={} sockets={}", p.username(), p.sessionId(), count);
    }

    /** Gọi khi socket đóng. An toàn khi gọi nhiều lần. */
    public void unregister(String connectionId) {
        Connection conn = connections.remove(connectionId);
        if (conn == null) {
            return;
        }
        long userId = conn.principal().userId();
        Integer remaining = socketsPerUser.computeIfPresent(userId, (k, v) -> v <= 1 ? null : v - 1);
        if (remaining == null) {
            broadcastPresence(conn.principal(), false, 0);
        }
        log.debug("WS closed user={} remaining={}", conn.principal().username(), remaining == null ? 0 : remaining);
    }

    // ------------------------------------------------------------------ thu hồi / ép logout

    public void disconnectSession(long sessionId, String reason) {
        revokeWhere(c -> c.principal().sessionId() == sessionId, reason);
    }

    public void disconnectUser(long userId, String reason) {
        revokeWhere(c -> c.principal().userId() == userId, reason);
    }

    public void disconnectUserExcept(long userId, long keepSessionId, String reason) {
        revokeWhere(c -> c.principal().userId() == userId && c.principal().sessionId() != keepSessionId, reason);
    }

    /** Gửi session.revoked rồi đóng socket với mã 4001. */
    public void revokeAndClose(Connection conn, String reason) {
        send(conn, RealtimeEvent.of(RealtimeEvent.SESSION_REVOKED, Map.of("reason", reason)));
        try {
            conn.ws().close(new CloseStatus(CLOSE_SESSION_REVOKED, "session revoked"));
        } catch (IOException | RuntimeException e) {
            log.debug("Error closing socket {}: {}", conn.id(), e.getMessage());
        }
        unregister(conn.id());
    }

    private void revokeWhere(Predicate<Connection> filter, String reason) {
        for (Connection c : List.copyOf(connections.values())) {
            if (filter.test(c)) {
                revokeAndClose(c, reason);
            }
        }
    }

    // ------------------------------------------------------------------ gửi sự kiện

    public void sendToUser(long userId, RealtimeEvent event) {
        connections.values().stream()
                .filter(c -> c.principal().userId() == userId)
                .forEach(c -> send(c, event));
    }

    public void broadcastToPermission(String permission, RealtimeEvent event) {
        connections.values().stream()
                .filter(c -> permissions.has(c.principal().role(), permission))
                .forEach(c -> send(c, event));
    }

    public void send(Connection conn, RealtimeEvent event) {
        if (!conn.ws().isOpen()) {
            return;
        }
        try {
            conn.ws().sendMessage(new TextMessage(mapper.writeValueAsString(event)));
        } catch (JsonProcessingException e) {
            log.error("Cannot serialize event {}", event.type(), e);
        } catch (IOException | RuntimeException e) {
            log.debug("Send failed on socket {}: {}", conn.id(), e.getMessage());
        }
    }

    public void ping(Connection conn) {
        if (!conn.ws().isOpen()) {
            return;
        }
        try {
            conn.ws().sendMessage(new PingMessage());
        } catch (IOException | RuntimeException e) {
            log.debug("Ping failed on socket {}: {}", conn.id(), e.getMessage());
        }
    }

    private void broadcastPresence(AuthPrincipal p, boolean online, int sockets) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", p.publicId());
        data.put("username", p.username());
        data.put("fullName", p.fullName());
        data.put("role", p.role().code());
        data.put("online", online);
        data.put("connections", sockets);
        broadcastToPermission(Permissions.PRESENCE_VIEW, RealtimeEvent.of(RealtimeEvent.PRESENCE_CHANGED, data));
    }

    // ------------------------------------------------------------------ truy vấn

    public Optional<Connection> find(String connectionId) {
        return Optional.ofNullable(connections.get(connectionId));
    }

    public Collection<Connection> snapshot() {
        return List.copyOf(connections.values());
    }

    /** Danh sách user đang online, gộp theo user. */
    public List<OnlineUser> onlineUsers() {
        Map<Long, List<Connection>> byUser = new LinkedHashMap<>();
        for (Connection c : connections.values()) {
            byUser.computeIfAbsent(c.principal().userId(), k -> new ArrayList<>()).add(c);
        }
        List<OnlineUser> result = new ArrayList<>();
        for (List<Connection> conns : byUser.values()) {
            AuthPrincipal p = conns.get(0).principal();
            Instant since = conns.stream().map(Connection::connectedAt).min(Instant::compareTo).orElse(null);
            result.add(new OnlineUser(p.publicId(), p.username(), p.fullName(), p.role().code(), conns.size(),
                    since));
        }
        return result;
    }

    public int onlineUserCount() {
        return socketsPerUser.size();
    }
}
