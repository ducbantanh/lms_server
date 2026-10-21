package lms.submission;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.common.RequestInfo;
import lms.submission.SubmissionViews.AssessmentSubmissions;
import lms.submission.SubmissionViews.GradeRequest;
import lms.submission.SubmissionViews.SubmissionRow;

/** API giảng viên: danh sách bài nộp của một bài và chấm điểm thủ công. */
@RestController
@RequestMapping("/api/v1/teacher/courses/{courseId}/assessments/{assessmentId}/submissions")
@RequirePermission({ Permissions.COURSES_MANAGE, Permissions.SUBMISSIONS_MANAGE })
public class TeacherSubmissionController {

    private final GradingService grading;

    public TeacherSubmissionController(GradingService grading) {
        this.grading = grading;
    }

    @GetMapping
    public ApiResponse<AssessmentSubmissions> list(@PathVariable String courseId, @PathVariable String assessmentId,
            AuthPrincipal me) {
        return ApiResponse.ok(grading.list(courseId, assessmentId, me));
    }

    @PutMapping("/{studentId}/grade")
    public ApiResponse<SubmissionRow> grade(@PathVariable String courseId, @PathVariable String assessmentId,
            @PathVariable String studentId, @Valid @RequestBody GradeRequest body, AuthPrincipal me,
            HttpServletRequest req) {
        return ApiResponse.ok(grading.grade(courseId, assessmentId, studentId, body, me, RequestInfo.clientIp(req)));
    }
}
