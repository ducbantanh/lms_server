package lms.progress;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.progress.ProgressViews.CourseProgress;

/** API sinh viên: tiến độ của mình ở một khóa học đã ghi danh, đánh dấu bài học đã học. */
@RestController
@RequestMapping("/api/v1/student/courses/{courseId}")
@RequirePermission(Permissions.COURSES_VIEW)
public class StudentProgressController {

    private final ProgressService service;

    public StudentProgressController(ProgressService service) {
        this.service = service;
    }

    @GetMapping("/progress")
    public ApiResponse<CourseProgress> progress(@PathVariable String courseId, AuthPrincipal me) {
        return ApiResponse.ok(service.forCourse(courseId, me));
    }

    @PutMapping("/lessons/{lessonId}/completion")
    public ApiResponse<CourseProgress> complete(@PathVariable String courseId, @PathVariable String lessonId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.setCompleted(courseId, lessonId, true, me));
    }

    @DeleteMapping("/lessons/{lessonId}/completion")
    public ApiResponse<CourseProgress> uncomplete(@PathVariable String courseId, @PathVariable String lessonId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.setCompleted(courseId, lessonId, false, me));
    }
}
