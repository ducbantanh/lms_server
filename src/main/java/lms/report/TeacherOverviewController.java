package lms.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lms.auth.AuthPrincipal;
import lms.auth.Permissions;
import lms.auth.RequirePermission;
import lms.common.ApiResponse;
import lms.report.OverviewViews.TeacherOverview;

/** API trang /teacher/: bài chờ chấm, lịch bài, tình hình từng khóa học. */
@RestController
@RequestMapping("/api/v1/teacher/overview")
@RequirePermission(Permissions.COURSES_MANAGE)
public class TeacherOverviewController {

    private final OverviewService service;

    public TeacherOverviewController(OverviewService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<TeacherOverview> overview(AuthPrincipal me) {
        return ApiResponse.ok(service.overview(me));
    }
}
