package lms.analytics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;

/**
 * Controller cung cấp API phân tích phổ điểm và số liệu học phần.
 */
@RestController
@RequestMapping("/api/v1/teacher/analytics")
public class AnalyticsController {

    private final CourseAnalyticsService analyticsService;

    public AnalyticsController(CourseAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Lấy phân tích phổ điểm, độ lệch chuẩn và tỷ lệ đạt của một bài kiểm tra.
     */
    @GetMapping("/assessments/{assessmentPublicId}")
    @RequirePermission(Permissions.COURSES_MANAGE)
    public ApiResponse<CourseStatistics> getAssessmentAnalytics(
            @PathVariable("assessmentPublicId") String assessmentPublicId) {
        CourseStatistics stats = analyticsService.calculateForAssessment(assessmentPublicId);
        return ApiResponse.ok(stats);
    }
}
