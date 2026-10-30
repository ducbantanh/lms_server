package lms.code.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeMetricsAnalyzerTest {

    private final CodeMetricsAnalyzer analyzer = new CodeMetricsAnalyzer();

    @Test
    @DisplayName("Đoạn mã rỗng có độ phức tạp McCabe cơ bản bằng 1")
    void testEmptyCode() {
        CodeComplexityMetrics metrics = analyzer.analyze("", "java");
        assertThat(metrics.totalLines()).isEqualTo(0);
        assertThat(metrics.cyclomaticComplexity()).isEqualTo(1);
        assertThat(metrics.qualityGrade()).isEqualTo("A");
    }

    @Test
    @DisplayName("Đoạn mã tuyến tính không rẽ nhánh có độ phức tạp bằng 1")
    void testLinearCode() {
        String code = """
                public class Hello {
                    public static void main(String[] args) {
                        int x = 10;
                        int y = 20;
                        int z = x + y;
                        System.out.println(z);
                    }
                }
                """;

        CodeComplexityMetrics metrics = analyzer.analyze(code, "java");
        assertThat(metrics.cyclomaticComplexity()).isEqualTo(1);
        assertThat(metrics.maxNestingDepth()).isGreaterThan(0);
        assertThat(metrics.isAcceptableComplexity()).isTrue();
    }

    @Test
    @DisplayName("Đoạn mã có if-else, while, && sẽ tăng độ phức tạp McCabe tương ứng")
    void testBranchingCode() {
        String code = """
                public class DecisionTree {
                    public boolean check(int a, int b) {
                        // Kiem tra dieu kien logic
                        if (a > 0 && b > 0) {
                            while (a < b) {
                                a++;
                            }
                            return true;
                        } else if (a == 0) {
                            return false;
                        }
                        return false;
                    }
                }
                """;

        CodeComplexityMetrics metrics = analyzer.analyze(code, "java");
        // if (+1), && (+1), while (+1), else if (+1) -> base 1 + 4 = 5
        assertThat(metrics.cyclomaticComplexity()).isGreaterThanOrEqualTo(4);
        assertThat(metrics.commentLines()).isGreaterThanOrEqualTo(1);
        assertThat(metrics.keywordFrequencies()).containsKey("if");
        assertThat(metrics.keywordFrequencies()).containsKey("while");
    }

    @Test
    @DisplayName("Hỗ trợ phân tích mã Python theo độ thụt lề (indentation)")
    void testPythonCode() {
        String pyCode = """
                # Ham tinh tong
                def calculate(nums):
                    total = 0
                    for x in nums:
                        if x % 2 == 0:
                            total += x
                    return total
                """;

        CodeComplexityMetrics metrics = analyzer.analyze(pyCode, "python");
        assertThat(metrics.commentLines()).isGreaterThanOrEqualTo(1);
        assertThat(metrics.cyclomaticComplexity()).isGreaterThan(1);
        assertThat(metrics.maxNestingDepth()).isGreaterThanOrEqualTo(2);
    }
}
