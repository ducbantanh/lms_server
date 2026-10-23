package lms.student;

import java.util.List;
import java.util.Map;

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
import lms.course.Course;
import lms.course.CourseRepository;
import lms.course.CourseService;
import lms.course.CourseViews;
import lms.course.CourseViews.JoinRequest;
import lms.progress.ProgressService;
import lms.progress.ProgressViews.CourseProgress;
import lms.progress.ProgressViews.NextLesson;

/**
 * API sinh viên: dashboard (profile + khóa học đã ghi danh), xem nội dung khóa học, tham gia bằng mã.
 */
@RestController
@RequestMapping("/api/v1/student")
@RequirePermission(Permissions.COURSES_VIEW)
public class StudentController {

    public record Profile(String id, String username, String fullName) {
    }

    /**
     * Khớp model client (lms_client/lib/use-courses.ts). progress là phần trăm (null khi chưa có gì để tính),
     * nextLesson là bài học đầu tiên chưa học.
     */
    public record CourseSummary(String id, String code, String group, String title, String teacherName,
            Integer progress, NextLesson nextLesson) {
    }

    public record Dashboard(Profile profile, List<CourseSummary> courses) {
    }

    private final CourseRepository courses;
    private final CourseService courseService;
    private final ProgressService progress;

    public StudentController(CourseRepository courses, CourseService courseService, ProgressService progress) {
        this.courses = courses;
        this.courseService = courseService;
        this.progress = progress;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Dashboard> dashboard(AuthPrincipal me) {
        Profile profile = new Profile(me.publicId(), me.username(), me.fullName());
        List<Course> list = courses.findEnrolledBy(me.userId());
        Map<Long, CourseProgress> byCourse = progress.forCourses(me.userId(), list);
        List<CourseSummary> enrolled = list.stream()
                .map(c -> summary(c, byCourse.get(c.getId())))
                .toList();
        return ApiResponse.ok(new Dashboard(profile, enrolled));
    }

    /** Nội dung khóa học: ẩn tài liệu bị ẩn và đề của bài chưa mở. */
    @GetMapping("/courses/{id}")
    public ApiResponse<CourseViews.CourseDetail> course(@PathVariable String id, AuthPrincipal me) {
        return ApiResponse.ok(courseService.studentDetail(id, me));
    }

    @PostMapping("/courses/join")
    public ApiResponse<CourseViews.CourseSummary> join(@Valid @RequestBody JoinRequest body, AuthPrincipal me) {
        return ApiResponse.ok(courseService.join(body.key(), me));
    }

    private static CourseSummary summary(Course c, CourseProgress p) {
        String teacher = c.getTeacher() == null ? null : c.getTeacher().getFullName();
        return new CourseSummary(c.getPublicId(), c.getCode(), c.getGroupName(), c.getTitle(), teacher, p.percent(),
                p.nextLesson());
    }
}
