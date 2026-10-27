package lms.leave;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.leave.LeaveViews.CourseLeaves;
import lms.leave.LeaveViews.LeaveView;
import lms.leave.LeaveViews.ReviewRequest;
import lms.leave.LeaveViews.SettingsRequest;

/** Giảng viên: đơn xin nghỉ của khóa học, duyệt đơn, số giờ sinh viên phải báo trước. */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherLeaveController {

    private final LeaveService service;

    public TeacherLeaveController(LeaveService service) {
        this.service = service;
    }

    @GetMapping("/leave-requests")
    public ApiResponse<CourseLeaves> list(@PathVariable String courseId, AuthPrincipal me) {
        return ApiResponse.ok(service.forCourse(courseId, me));
    }

    @PostMapping("/leave-requests/{requestId}/review")
    public ApiResponse<LeaveView> review(@PathVariable String courseId, @PathVariable String requestId,
            @Valid @RequestBody ReviewRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.review(courseId, requestId, body.approve(), body.note(), me));
    }

    @PutMapping("/leave-settings")
    public ApiResponse<SettingsRequest> settings(@PathVariable String courseId,
            @Valid @RequestBody SettingsRequest body, AuthPrincipal me) {
        return ApiResponse.ok(new SettingsRequest(service.updateNoticeHours(courseId, body.noticeHours(), me)));
    }
}
