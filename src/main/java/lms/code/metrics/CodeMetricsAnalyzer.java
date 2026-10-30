package lms.code.metrics;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * Bộ phân tích tĩnh đo lường độ phức tạp McCabe Cyclomatic Complexity,
 * độ sâu lồng nhau (nesting depth) và chỉ số bảo trì phần mềm (Maintainability Index).
 */
@Component
public class CodeMetricsAnalyzer {

    private static final Set<String> DECISION_KEYWORDS = Set.of(
            "if", "for", "while", "case", "catch", "elif", "except"
    );

    private static final Pattern LOGICAL_OPERATORS_PATTERN = Pattern.compile("&&|\\|\\||\\band\\b|\\bor\\b|\\?");

    public CodeComplexityMetrics analyze(String code, String language) {
        if (code == null || code.isBlank()) {
            return new CodeComplexityMetrics(0, 0, 0, 0, 1, 0, 0.0, 100.0, "A", Map.of());
        }

        String lang = language == null ? "java" : language.toLowerCase().trim();
        String[] lines = code.split("\\R");
        int totalLines = lines.length;
        int blankLines = 0;
        int commentLines = 0;
        int codeLines = 0;

        boolean inBlockComment = false;
        int currentNesting = 0;
        int maxNesting = 0;
        int decisionPoints = 0;
        Map<String, Integer> keywordFreq = new LinkedHashMap<>();

        for (String rawLine : lines) {
            String trimmed = rawLine.trim();

            if (trimmed.isEmpty()) {
                blankLines++;
                continue;
            }

            // Xử lý chú thích nhiều dòng (C/Java style /* ... */)
            if (!inBlockComment && (trimmed.startsWith("/*") || trimmed.contains("/*"))) {
                inBlockComment = true;
                commentLines++;
                if (trimmed.contains("*/")) {
                    inBlockComment = false;
                }
                continue;
            }
            if (inBlockComment) {
                commentLines++;
                if (trimmed.contains("*/")) {
                    inBlockComment = false;
                }
                continue;
            }

            // Xử lý chú thích một dòng
            if (trimmed.startsWith("//") || trimmed.startsWith("#")) {
                commentLines++;
                continue;
            }

            codeLines++;

            // Đo độ sâu lồng nhau
            if (lang.equals("python")) {
                int indentSpaces = countLeadingSpaces(rawLine);
                int pythonDepth = indentSpaces / 4;
                if (pythonDepth > maxNesting) {
                    maxNesting = pythonDepth;
                }
            } else {
                for (char c : trimmed.toCharArray()) {
                    if (c == '{') {
                        currentNesting++;
                        if (currentNesting > maxNesting) {
                            maxNesting = currentNesting;
                        }
                    } else if (c == '}') {
                        if (currentNesting > 0) {
                            currentNesting--;
                        }
                    }
                }
            }

            // Tách từ để tìm từ khóa điều kiện
            String[] tokens = trimmed.split("[^a-zA-Z0-9_]+");
            for (String token : tokens) {
                if (token.isEmpty()) continue;
                if (DECISION_KEYWORDS.contains(token)) {
                    decisionPoints++;
                    keywordFreq.merge(token, 1, Integer::sum);
                }
            }

            // Đếm toán tử logic &&, ||, ?
            Matcher opMatcher = LOGICAL_OPERATORS_PATTERN.matcher(trimmed);
            while (opMatcher.find()) {
                decisionPoints++;
                keywordFreq.merge(opMatcher.group(), 1, Integer::sum);
            }
        }

        // Độ phức tạp McCabe: M = E - N + 2P (tương đương 1 + số điểm rẽ nhánh)
        int cyclomaticComplexity = 1 + decisionPoints;

        double commentRatio = totalLines > 0 ? (double) commentLines / totalLines : 0.0;

        // Ước tính Maintainability Index (MI)
        // MI = 171 - 5.2 * ln(V) - 0.23 * CC - 16.2 * ln(LOC)
        double locForFormula = Math.max(1, codeLines);
        double volumeEstimate = Math.max(1, (decisionPoints + 1) * 8.0);
        double rawMi = 171.0 - (5.2 * Math.log(volumeEstimate)) - (0.23 * cyclomaticComplexity) - (16.2 * Math.log(locForFormula));
        double maintainabilityIndex = Math.max(0.0, Math.min(100.0, (rawMi * 100.0) / 171.0));

        String grade = determineGrade(maintainabilityIndex, cyclomaticComplexity);

        return new CodeComplexityMetrics(
                totalLines,
                codeLines,
                commentLines,
                blankLines,
                cyclomaticComplexity,
                maxNesting,
                Math.round(commentRatio * 1000.0) / 10.0,
                Math.round(maintainabilityIndex * 10.0) / 10.0,
                grade,
                keywordFreq
        );
    }

    private int countLeadingSpaces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') count++;
            else if (c == '\t') count += 4;
            else break;
        }
        return count;
    }

    private String determineGrade(double mi, int cc) {
        if (mi >= 80.0 && cc <= 5) return "A";
        if (mi >= 65.0 && cc <= 10) return "B";
        if (mi >= 50.0 && cc <= 20) return "C";
        if (mi >= 35.0) return "D";
        return "F";
    }
}
