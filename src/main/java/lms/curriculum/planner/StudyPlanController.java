package lms.curriculum.planner;

import java.util.Set;

import org.springframework.web.bind.annotation.*;

import lms.common.ApiResponse;

/**
 * REST API sinh lộ trình học tập thông minh dựa trên đồ thị môn học tiên quyết.
 */
@RestController
@RequestMapping("/api/v1/curriculum/planner")
public class StudyPlanController {

    private final StudyPathPlanner planner;

    public StudyPlanController(StudyPathPlanner planner) {
        this.planner = planner;
    }

    public record GeneratePlanRequest(
            CareerTrack track,
            int maxCreditsPerSemester,
            Set<String> completedCourses
    ) {}

    @PostMapping("/generate")
    public ApiResponse<StudyPlanResult> generatePlan(@RequestBody GeneratePlanRequest req) {
        CareerTrack selectedTrack = req.track() != null ? req.track() : CareerTrack.SOFTWARE_ENGINEERING;
        int maxCredits = req.maxCreditsPerSemester() > 0 ? req.maxCreditsPerSemester() : 16;
        StudyPlanResult result = planner.planForTrack(selectedTrack, maxCredits, req.completedCourses());
        return ApiResponse.ok(result);
    }
}
