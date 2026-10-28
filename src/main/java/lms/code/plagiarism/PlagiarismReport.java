package lms.code.plagiarism;

import java.util.List;

/**
 * Báo cáo kết quả rà soát phát hiện trùng lặp mã nguồn giữa các sinh viên.
 */
public record PlagiarismReport(
        int totalSubmissionsAnalyzed,
        int totalComparisons,
        List<MatchPair> suspiciousPairs) {

    public record MatchPair(
            String student1Username,
            String student1FullName,
            String student2Username,
            String student2FullName,
            double similarityPercentage,
            int sharedFingerprintsCount,
            String verdict) {
    }
}
