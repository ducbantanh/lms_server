package lms.analytics;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CourseAnalyticsServiceTest {

    @Test
    @DisplayName("computeStatistics tính đúng các chỉ số thống kê phổ điểm")
    void testComputeStatistics() {
        List<Double> scores = List.of(9.0, 8.5, 7.5, 6.0, 5.0, 3.5, 10.0);
        CourseStatistics stats = CourseAnalyticsService.computeStatistics(scores, 10);

        assertThat(stats.totalStudents()).isEqualTo(10);
        assertThat(stats.gradedSubmissions()).isEqualTo(7);
        assertThat(stats.highestScore()).isEqualTo(10.0);
        assertThat(stats.lowestScore()).isEqualTo(3.5);
        assertThat(stats.meanScore()).isGreaterThan(7.0);
        assertThat(stats.medianScore()).isEqualTo(7.5);
        assertThat(stats.passRatePercent()).isGreaterThan(80.0); // 6/7 passed (>= 4.0)

        // Kiểm tra phân phối điểm chữ
        assertThat(stats.letterGradeDistribution().get("A")).isEqualTo(3); // 8.5, 9.0, 10.0
        assertThat(stats.letterGradeDistribution().get("B")).isEqualTo(1); // 7.5
        assertThat(stats.letterGradeDistribution().get("C")).isEqualTo(1); // 6.0
        assertThat(stats.letterGradeDistribution().get("D")).isEqualTo(1); // 5.0
        assertThat(stats.letterGradeDistribution().get("F")).isEqualTo(1); // 3.5

        // Kiểm tra histogram
        assertThat(stats.scoreHistogram()).hasSize(5);
    }

    @Test
    @DisplayName("computeStatistics xử lý an toàn khi danh sách điểm rỗng")
    void testEmptyScores() {
        CourseStatistics stats = CourseAnalyticsService.computeStatistics(List.of(), 50);

        assertThat(stats.totalStudents()).isEqualTo(50);
        assertThat(stats.gradedSubmissions()).isEqualTo(0);
        assertThat(stats.meanScore()).isEqualTo(0.0);
        assertThat(stats.medianScore()).isEqualTo(0.0);
        assertThat(stats.passRatePercent()).isEqualTo(0.0);
        assertThat(stats.letterGradeDistribution().get("A")).isEqualTo(0);
    }
}
