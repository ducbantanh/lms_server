package lms.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Bộ so khớp kết quả đầu ra chấm lập trình (Code Output Comparison & Diff Checker).
 * Tuân thủ tiêu chuẩn các hệ thống Online Judge phổ biến (Themis, Codeforces, VNOI):
 * - Bỏ qua khác biệt xuống dòng (\r\n, \r, \n).
 * - Bỏ qua khoảng trắng cuối mỗi dòng (trailing spaces).
 * - Bỏ qua các dòng trống dư thừa ở cuối file.
 * - Hỗ trợ so khớp mở rộng: dung sai số thực (epsilon tolerance), chuẩn hóa khoảng trắng, và tạo chi tiết khác biệt (diff summary).
 */
public final class OutputChecker {

    public enum ComparisonMode {
        STANDARD,
        STRICT,
        COLLAPSE_WHITESPACE,
        IGNORE_CASE,
        NUMERIC_FLOAT_TOLERANCE
    }

    public record DiffResult(
            boolean matched,
            int firstDiffLineIndex,
            String expectedLine,
            String actualLine,
            String summaryMessage) {

        public static DiffResult match() {
            return new DiffResult(true, -1, null, null, "Khớp hoàn toàn");
        }

        public static DiffResult mismatch(int lineIndex, String expected, String actual, String message) {
            return new DiffResult(false, lineIndex, expected, actual, message);
        }
    }

    private OutputChecker() {
    }

    /**
     * So khớp chuẩn mặc định cho hệ chấm.
     */
    public static boolean matches(String actual, String expected) {
        if (actual == null && expected == null) {
            return true;
        }
        if (actual == null || expected == null) {
            return false;
        }
        return lines(actual).equals(lines(expected));
    }

    /**
     * So khớp theo chế độ tùy biến (Comparison Mode).
     */
    public static boolean matches(String actual, String expected, ComparisonMode mode) {
        if (mode == null || mode == ComparisonMode.STANDARD) {
            return matches(actual, expected);
        }

        if (actual == null || expected == null) {
            return Objects.equals(actual, expected);
        }

        return switch (mode) {
            case STRICT -> actual.equals(expected);
            case IGNORE_CASE -> lines(actual.toLowerCase()).equals(lines(expected.toLowerCase()));
            case COLLAPSE_WHITESPACE -> collapseWhitespace(actual).equals(collapseWhitespace(expected));
            case NUMERIC_FLOAT_TOLERANCE -> matchesNumericTolerance(actual, expected, 1e-6);
            default -> matches(actual, expected);
        };
    }

    /**
     * So khớp với dung sai số thực (floating point tolerance epsilon).
     */
    public static boolean matchesNumericTolerance(String actual, String expected, double epsilon) {
        List<String> actualLines = lines(actual);
        List<String> expectedLines = lines(expected);

        if (actualLines.size() != expectedLines.size()) {
            return false;
        }

        for (int i = 0; i < actualLines.size(); i++) {
            String[] actTokens = actualLines.get(i).trim().split("\\s+");
            String[] expTokens = expectedLines.get(i).trim().split("\\s+");

            if (actTokens.length != expTokens.length) {
                return false;
            }

            for (int j = 0; j < actTokens.length; j++) {
                String a = actTokens[j];
                String e = expTokens[j];

                if (a.equals(e)) {
                    continue;
                }

                // Thử parse số thực
                try {
                    double da = Double.parseDouble(a);
                    double de = Double.parseDouble(e);
                    if (Math.abs(da - de) > epsilon && Math.abs(da - de) / Math.max(1.0, Math.abs(de)) > epsilon) {
                        return false;
                    }
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Tạo báo cáo so sánh chi tiết phát hiện dòng sai lệch đầu tiên.
     */
    public static DiffResult compareWithDiff(String actual, String expected) {
        if (actual == null && expected == null) {
            return DiffResult.match();
        }
        if (actual == null) {
            return DiffResult.mismatch(1, "<expected>", "<null>", "Không có dữ liệu thực tế");
        }
        if (expected == null) {
            return DiffResult.mismatch(1, "<null>", "<actual>", "Dữ liệu kỳ vọng trống");
        }

        List<String> actualLines = lines(actual);
        List<String> expectedLines = lines(expected);

        int minSize = Math.min(actualLines.size(), expectedLines.size());
        for (int i = 0; i < minSize; i++) {
            String actLine = actualLines.get(i);
            String expLine = expectedLines.get(i);
            if (!actLine.equals(expLine)) {
                return DiffResult.mismatch(
                        i + 1,
                        expLine,
                        actLine,
                        "Sai lệch ở dòng " + (i + 1) + ": kỳ vọng '" + expLine + "' nhưng nhận được '" + actLine + "'"
                );
            }
        }

        if (actualLines.size() < expectedLines.size()) {
            int missingIndex = actualLines.size() + 1;
            return DiffResult.mismatch(
                    missingIndex,
                    expectedLines.get(actualLines.size()),
                    "<kết thúc>",
                    "Thiếu dòng kết quả từ dòng " + missingIndex
            );
        }

        if (actualLines.size() > expectedLines.size()) {
            int extraIndex = expectedLines.size() + 1;
            return DiffResult.mismatch(
                    extraIndex,
                    "<kết thúc>",
                    actualLines.get(expectedLines.size()),
                    "Dư thừa dòng kết quả từ dòng " + extraIndex
            );
        }

        return DiffResult.match();
    }

    /**
     * Tách văn bản thành danh sách các dòng chuẩn, loại bỏ trailing whitespaces và dòng trống cuối file.
     */
    public static List<String> lines(String text) {
        if (text == null) {
            return new ArrayList<>();
        }
        List<String> out = new ArrayList<>();
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        for (String line : normalized.split("\n", -1)) {
            out.add(line.stripTrailing());
        }
        while (!out.isEmpty() && out.get(out.size() - 1).isEmpty()) {
            out.remove(out.size() - 1);
        }
        return out;
    }

    private static String collapseWhitespace(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replaceAll("\\s+", " ");
    }
}
