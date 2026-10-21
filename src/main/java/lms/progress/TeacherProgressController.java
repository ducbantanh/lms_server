package lms.progress;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.progress.ProgressViews.CourseProgressReport;

/** API giảng viên: tiến độ của mọi sinh viên trong khóa học mình quản lý. */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}/progress")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherProgressController {

    private final ProgressService service;

    public TeacherProgressController(ProgressService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<CourseProgressReport> report(@PathVariable String courseId, AuthPrincipal me) {
        return ApiResponse.ok(service.forStudents(courseId, me));
    }
}
