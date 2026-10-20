package lms.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.PageResult;
import lms.notification.NotificationViews.NotificationView;
import lms.notification.NotificationViews.Summary;

/** Thông báo của người đang đăng nhập: danh sách theo loại, số chưa đọc, đánh dấu đã đọc. */
@RestController
@RequestMapping("/api/v1/notifications")
@RequirePermission(Permissions.COURSES_VIEW)
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResult<NotificationView>> list(@RequestParam(required = false) String category,
            @RequestParam(defaultValue = "false") boolean unread, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, AuthPrincipal me) {
        return ApiResponse.ok(service.list(me, category, unread, page, size));
    }

    @GetMapping("/summary")
    public ApiResponse<Summary> summary(AuthPrincipal me) {
        return ApiResponse.ok(service.summary(me));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable String id, AuthPrincipal me) {
        service.markRead(id, me);
        return ApiResponse.ok();
    }

    @PostMapping("/read-all")
    public ApiResponse<Void> markAllRead(AuthPrincipal me) {
        service.markAllRead(me);
        return ApiResponse.ok();
    }
}
