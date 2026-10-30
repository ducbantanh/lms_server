package lms.code.metrics;

import java.util.Map;

/**
 * Thước đo độ phức tạp và chất lượng của mã nguồn do sinh viên nộp.
 */
public record CodeComplexityMetrics(
        int totalLines,
        int codeLines,
        int commentLines,
        int blankLines,
        int cyclomaticComplexity,
        int maxNestingDepth,
        double commentRatio,
        double maintainabilityIndex,
        String qualityGrade,
        Map<String, Integer> keywordFrequencies
) {
    public boolean isAcceptableComplexity() {
        return cyclomaticComplexity <= 15 && maxNestingDepth <= 5;
    }
}
