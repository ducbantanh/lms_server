package lms.code.plagiarism;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Dịch vụ phát hiện gian lận và sao chép mã nguồn bài tập lập trình (Code Plagiarism Detection Service).
 */
@Service
public class PlagiarismDetectionService {

    private static final Logger log = LoggerFactory.getLogger(PlagiarismDetectionService.class);

    public record SubmissionInput(
            String studentUsername,
            String studentFullName,
            String sourceCode) {
    }

    /**
     * So sánh chéo toàn bộ các bài nộp trong cùng một câu hỏi lập trình và xếp hạng các cặp nghi vấn.
     *
     * @param submissions        Danh sách bài nộp của sinh viên
     * @param thresholdPercentage Ngưỡng cảnh báo trùng khớp (ví dụ: 70%)
     * @return Báo cáo chi tiết các cặp trùng lặp
     */
    public PlagiarismReport analyzeBatch(List<SubmissionInput> submissions, double thresholdPercentage) {
        if (submissions == null || submissions.size() < 2) {
            return new PlagiarismReport(submissions != null ? submissions.size() : 0, 0, Collections.emptyList());
        }

        int n = submissions.size();
        List<Set<Long>> fingerprintsList = new ArrayList<>(n);

        // Tiền xử lý và tạo fingerprint cho từng bài nộp
        for (SubmissionInput sub : submissions) {
            List<String> tokens = CodeTokenizer.tokenizeToRepresentation(sub.sourceCode());
            Set<Long> fp = WinnowingFingerprinter.computeFingerprints(tokens);
            fingerprintsList.add(fp);
        }

        List<PlagiarismReport.MatchPair> matches = new ArrayList<>();
        int totalComparisons = 0;

        // So khớp từng cặp bài (All-Pairs Comparison O(N^2))
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                totalComparisons++;
                Set<Long> fp1 = fingerprintsList.get(i);
                Set<Long> fp2 = fingerprintsList.get(j);

                double simScore = WinnowingFingerprinter.calculateSimilarity(fp1, fp2);
                double simPct = Math.round(simScore * 1000.0) / 10.0;

                if (simPct >= thresholdPercentage) {
                    Set<Long> intersection = new java.util.HashSet<>(fp1);
                    intersection.retainAll(fp2);

                    String verdict = simPct >= 90.0 ? "NGHI VẤN CAO (GẦN NHƯ TRÙNG TUYỆT ĐỐI)" :
                            simPct >= 75.0 ? "ĐÁNG NGỜ (CÙNG CẤU TRÚC GIẢI THUẬT)" :
                            "TRÙNG KHỚP CỤC BỘ";

                    matches.add(new PlagiarismReport.MatchPair(
                            submissions.get(i).studentUsername(),
                            submissions.get(i).studentFullName(),
                            submissions.get(j).studentUsername(),
                            submissions.get(j).studentFullName(),
                            simPct,
                            intersection.size(),
                            verdict
                    ));
                }
            }
        }

        // Sắp xếp các cặp theo tỷ lệ trùng khớp giảm dần
        matches.sort((a, b) -> Double.compare(b.similarityPercentage(), a.similarityPercentage()));

        log.info("Phân tích sao chép mã nguồn: hoàn thành {} bài nộp, {} phép so sánh, phát hiện {} cặp nghi vấn (ngưỡng {}%)",
                n, totalComparisons, matches.size(), thresholdPercentage);

        return new PlagiarismReport(n, totalComparisons, matches);
    }
}
