package lms.realtime;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;

/**
 * Ảnh chụp danh sách online khi admin vừa mở dashboard.
 * Sau đó cập nhật bằng sự kiện presence.changed qua WebSocket, không polling.
 */
@RestController
@RequestMapping("/api/v1/admin/presence")
@RequirePermission(Permissions.PRESENCE_VIEW)
public class PresenceController {

    public record PresenceSnapshot(int onlineUsers, List<RealtimeHub.OnlineUser> users) {
    }

    private final RealtimeHub hub;

    public PresenceController(RealtimeHub hub) {
        this.hub = hub;
    }

    @GetMapping
    public ApiResponse<PresenceSnapshot> snapshot() {
        List<RealtimeHub.OnlineUser> users = hub.onlineUsers();
        return ApiResponse.ok(new PresenceSnapshot(users.size(), users));
    }
}
