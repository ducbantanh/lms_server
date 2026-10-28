package lms.quiz;

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
import lms.course.CourseViews.AssessmentView;
import lms.quiz.QuizViews.CreateQuizRequest;
import lms.quiz.QuizViews.StudentReview;
import lms.quiz.QuizViews.TeacherQuiz;

/** API giảng viên: tạo bài trắc nghiệm từ ngân hàng câu hỏi, xem đề và bài làm của sinh viên. */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherQuizController {

    private final QuizService service;

    public TeacherQuizController(QuizService service) {
        this.service = service;
    }

    @PostMapping("/quizzes")
    public ApiResponse<AssessmentView> create(@PathVariable String courseId,
            @Valid @RequestBody CreateQuizRequest body, AuthPrincipal me) {
        return ApiResponse.ok(service.create(courseId, body, me));
    }

    @GetMapping("/assessments/{assessmentId}/quiz")
    public ApiResponse<TeacherQuiz> quiz(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(service.teacherQuiz(courseId, assessmentId, me));
    }

    @GetMapping("/assessments/{assessmentId}/submissions/{studentId}/quiz")
    public ApiResponse<StudentReview> review(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String studentId, AuthPrincipal me) {
        return ApiResponse.ok(service.studentReview(courseId, assessmentId, studentId, me));
    }
}
