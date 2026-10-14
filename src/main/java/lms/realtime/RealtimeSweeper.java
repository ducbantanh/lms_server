package lms.realtime;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lms.auth.session.SessionService;

/**
 * Chạy mỗi 30 giây:
 * - Gửi WebSocket ping để giữ kết nối (proxy/idle timeout) và phát hiện client chết.
 * - Đóng socket có session đã hết hạn / bị revoke / user bị khóa (phòng trường hợp bị sót).
 */
@Component
public class RealtimeSweeper {

    private static final String REASON_EXPIRED = "expired";

    private final RealtimeHub hub;
    private final SessionService sessions;

    public RealtimeSweeper(RealtimeHub hub, SessionService sessions) {
        this.hub = hub;
        this.sessions = sessions;
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    public void sweep() {
        Collection<RealtimeHub.Connection> conns = hub.snapshot();
        if (conns.isEmpty()) {
            return;
        }
        Set<Long> sessionIds = conns.stream()
                .map(c -> c.principal().sessionId())
                .collect(Collectors.toSet());
        Set<Long> valid = sessions.filterValid(sessionIds);

        for (RealtimeHub.Connection c : conns) {
            if (!valid.contains(c.principal().sessionId())) {
                hub.revokeAndClose(c, REASON_EXPIRED);
            } else {
                hub.ping(c);
            }
        }
    }
}
