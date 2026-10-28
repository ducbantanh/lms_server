package lms.analytics;

import java.util.List;
import java.util.Map;

/**
 * Thống kê định lượng phổ điểm và hiệu suất học tập của học phần.
 */
public record CourseStatistics(
        long totalStudents,
        long gradedSubmissions,
        double meanScore,
        double medianScore,
        double standardDeviation,
        double highestScore,
        double lowestScore,
        double passRatePercent,
        Map<String, Long> letterGradeDistribution,
        List<HistogramBin> scoreHistogram) {

    public record HistogramBin(
            String rangeLabel,
            double minScore,
            double maxScore,
            long count,
            double percentage) {
    }
}
