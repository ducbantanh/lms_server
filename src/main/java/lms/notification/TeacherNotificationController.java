package lms.notification;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.notification.NotificationViews.AnnouncementRequest;
import lms.notification.NotificationViews.SentView;

/** API giảng viên: gửi thông báo tùy chỉnh và xem các thông báo đã gửi của khóa học. */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}/notifications")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherNotificationController {

    private final NotificationService service;

    public TeacherNotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<SentView>> history(@PathVariable String courseId, AuthPrincipal me) {
        return ApiResponse.ok(service.history(courseId, me));
    }

    @PostMapping
    public ApiResponse<SentView> announce(@PathVariable String courseId, @Valid @RequestBody AnnouncementRequest body,
            AuthPrincipal me) {
        return ApiResponse.ok(service.announce(courseId, body, me));
    }
}
