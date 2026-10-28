package lms.analytics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lms.common.AppException;
import lms.common.ErrorCode;
import lms.course.Assessment;
import lms.course.AssessmentRepository;
import lms.submission.Submission;
import lms.submission.SubmissionRepository;

/**
 * Dịch vụ phân tích thống kê điểm số và phổ điểm học phần (Course Performance Analytics).
 */
@Service
public class CourseAnalyticsService {

    private final AssessmentRepository assessmentRepository;
    private final SubmissionRepository submissionRepository;

    public CourseAnalyticsService(
            AssessmentRepository assessmentRepository,
            SubmissionRepository submissionRepository) {
        this.assessmentRepository = assessmentRepository;
        this.submissionRepository = submissionRepository;
    }

    /**
     * Phân tích thống kê kết quả thi / bài tập của một bài kiểm tra.
     */
    @Transactional(readOnly = true)
    public CourseStatistics calculateForAssessment(String assessmentPublicId) {
        Assessment assessment = assessmentRepository.findByPublicId(assessmentPublicId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy bài kiểm tra"));

        List<Submission> submissions = submissionRepository.findByAssessmentId(assessment.getId());
        List<Double> scores = submissions.stream()
                .map(Submission::getScore)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .toList();

        return computeStatistics(scores, submissions.size());
    }

    /**
     * Tính toán các chỉ số thống kê định lượng từ danh sách điểm số thang 10.
     */
    public static CourseStatistics computeStatistics(List<Double> rawScores, long totalStudents) {
        if (rawScores == null || rawScores.isEmpty()) {
            return new CourseStatistics(
                    totalStudents,
                    0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    0.0,
                    createEmptyLetterDistribution(),
                    createEmptyHistogram()
            );
        }

        List<Double> scores = new ArrayList<>(rawScores);
        Collections.sort(scores);

        int count = scores.size();
        double sum = 0.0;
        double min = scores.get(0);
        double max = scores.get(count - 1);

        for (double s : scores) {
            sum += s;
        }

        double mean = Math.round((sum / count) * 100.0) / 100.0;

        // Tính trung vị (Median)
        double median;
        if (count % 2 == 0) {
            median = Math.round(((scores.get(count / 2 - 1) + scores.get(count / 2)) / 2.0) * 100.0) / 100.0;
        } else {
            median = scores.get(count / 2);
        }

        // Tính độ lệch chuẩn (Standard Deviation)
        double varianceSum = 0.0;
        for (double s : scores) {
            varianceSum += Math.pow(s - mean, 2);
        }
        double stdDev = Math.round(Math.sqrt(varianceSum / count) * 100.0) / 100.0;

        // Phân phối điểm chữ chuẩn tín chỉ Việt Nam
        // A (>= 8.5), B (7.0 - 8.4), C (5.5 - 6.9), D (4.0 - 5.4), F (< 4.0)
        long countA = 0;
        long countB = 0;
        long countC = 0;
        long countD = 0;
        long countF = 0;

        for (double s : scores) {
            if (s >= 8.5) {
                countA++;
            } else if (s >= 7.0) {
                countB++;
            } else if (s >= 5.5) {
                countC++;
            } else if (s >= 4.0) {
                countD++;
            } else {
                countF++;
            }
        }

        Map<String, Long> letterMap = new LinkedHashMap<>();
        letterMap.put("A", countA);
        letterMap.put("B", countB);
        letterMap.put("C", countC);
        letterMap.put("D", countD);
        letterMap.put("F", countF);

        // Tỷ lệ đạt (Pass Rate: điểm >= 4.0)
        long passedCount = count - countF;
        double passRate = Math.round(((double) passedCount / count * 100.0) * 10.0) / 10.0;

        // Xây dựng phổ điểm Histogram (5 khoảng: [0, 2), [2, 4), [4, 6), [6, 8), [8, 10])
        List<CourseStatistics.HistogramBin> histogram = buildHistogram(scores);

        return new CourseStatistics(
                totalStudents,
                count,
                mean,
                median,
                stdDev,
                max,
                min,
                passRate,
                letterMap,
                histogram
        );
    }

    private static List<CourseStatistics.HistogramBin> buildHistogram(List<Double> sortedScores) {
        long[] bins = new long[5];
        int total = sortedScores.size();

        for (double s : sortedScores) {
            if (s < 2.0) {
                bins[0]++;
            } else if (s < 4.0) {
                bins[1]++;
            } else if (s < 6.0) {
                bins[2]++;
            } else if (s < 8.0) {
                bins[3]++;
            } else {
                bins[4]++;
            }
        }

        List<CourseStatistics.HistogramBin> list = new ArrayList<>();
        list.add(new CourseStatistics.HistogramBin("0.0 - 1.9", 0.0, 1.99, bins[0], calcPercent(bins[0], total)));
        list.add(new CourseStatistics.HistogramBin("2.0 - 3.9", 2.0, 3.99, bins[1], calcPercent(bins[1], total)));
        list.add(new CourseStatistics.HistogramBin("4.0 - 5.9", 4.0, 5.99, bins[2], calcPercent(bins[2], total)));
        list.add(new CourseStatistics.HistogramBin("6.0 - 7.9", 6.0, 7.99, bins[3], calcPercent(bins[3], total)));
        list.add(new CourseStatistics.HistogramBin("8.0 - 10.0", 8.0, 10.0, bins[4], calcPercent(bins[4], total)));
        return list;
    }

    private static double calcPercent(long count, int total) {
        if (total == 0) return 0.0;
        return Math.round(((double) count / total * 100.0) * 10.0) / 10.0;
    }

    private static Map<String, Long> createEmptyLetterDistribution() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("A", 0L);
        map.put("B", 0L);
        map.put("C", 0L);
        map.put("D", 0L);
        map.put("F", 0L);
        return map;
    }

    private static List<CourseStatistics.HistogramBin> createEmptyHistogram() {
        List<CourseStatistics.HistogramBin> list = new ArrayList<>();
        list.add(new CourseStatistics.HistogramBin("0.0 - 1.9", 0.0, 1.99, 0L, 0.0));
        list.add(new CourseStatistics.HistogramBin("2.0 - 3.9", 2.0, 3.99, 0L, 0.0));
        list.add(new CourseStatistics.HistogramBin("4.0 - 5.9", 4.0, 5.99, 0L, 0.0));
        list.add(new CourseStatistics.HistogramBin("6.0 - 7.9", 6.0, 7.99, 0L, 0.0));
        list.add(new CourseStatistics.HistogramBin("8.0 - 10.0", 8.0, 10.0, 0L, 0.0));
        return list;
    }
}
