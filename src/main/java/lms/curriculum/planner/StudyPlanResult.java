package lms.curriculum.planner;

import java.util.List;
import java.util.Map;

/**
 * Kế hoạch học tập đề xuất theo từng học kỳ dựa trên đồ thị tiên quyết.
 */
public record StudyPlanResult(
        String trackName,
        int totalSemesters,
        int totalCredits,
        List<SemesterSchedule> semesterSchedules,
        List<String> bottleneckCourses,
        Map<String, List<String>> prerequisiteGraph
) {
    public record SemesterSchedule(
            int semesterNumber,
            int totalCredits,
            List<CourseScheduleItem> courses
    ) {}

    public record CourseScheduleItem(
            String courseCode,
            String courseName,
            int credits,
            List<String> prerequisites
    ) {}
}
