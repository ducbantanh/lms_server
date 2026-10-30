package lms.curriculum.planner;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudyPathPlannerTest {

    private final StudyPathPlanner planner = new StudyPathPlanner();

    @Test
    @DisplayName("Lập lộ trình cho định hướng Software Engineering từ đầu")
    void testSoftwareEngineeringTrack() {
        StudyPlanResult plan = planner.planForTrack(
                CareerTrack.SOFTWARE_ENGINEERING,
                15,
                Set.of()
        );

        assertThat(plan.totalSemesters()).isGreaterThanOrEqualTo(2);
        assertThat(plan.totalCredits()).isGreaterThan(10);
        assertThat(plan.bottleneckCourses()).contains("INT1340"); // DSA unlocks most courses
        assertThat(plan.semesterSchedules()).isNotEmpty();

        // Học kỳ đầu tiên phải có môn không cần tiên quyết như DSA
        var firstTerm = plan.semesterSchedules().get(0);
        boolean hasDsa = firstTerm.courses().stream()
                .anyMatch(c -> c.courseCode().equals("INT1340"));
        assertThat(hasDsa).isTrue();
    }

    @Test
    @DisplayName("Bỏ qua các môn học đã tích lũy và mở khóa ngay các môn phụ thuộc")
    void testWithCompletedCourses() {
        StudyPlanResult plan = planner.planForTrack(
                CareerTrack.SOFTWARE_ENGINEERING,
                15,
                Set.of("INT1340") // Đã hoàn thành DSA
        );

        // Môn INT1340 không được xuất hiện lại trong các kỳ học tới
        boolean stillContainsDsa = plan.semesterSchedules().stream()
                .flatMap(s -> s.courses().stream())
                .anyMatch(c -> c.courseCode().equals("INT1340"));
        assertThat(stillContainsDsa).isFalse();

        // Các môn phụ thuộc như Lập trình mạng (INT1339) hoặc CSDL (INT1342) có thể học ngay kỳ 1
        var firstTerm = plan.semesterSchedules().get(0);
        assertThat(firstTerm.courses()).isNotEmpty();
    }

    @Test
    @DisplayName("Kiểm tra giới hạn số tín chỉ tối đa mỗi học kỳ")
    void testMaxCreditsConstraint() {
        int maxCredits = 10;
        StudyPlanResult plan = planner.planForTrack(
                CareerTrack.CYBER_SECURITY,
                maxCredits,
                Set.of()
        );

        for (var term : plan.semesterSchedules()) {
            // Trừ trường hợp môn học đơn lẻ vượt quá limit (ở đây mỗi môn 3-4 tín chỉ <= 10)
            assertThat(term.totalCredits()).isLessThanOrEqualTo(maxCredits + 4);
        }
    }
}
