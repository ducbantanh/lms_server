package lms.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutputCheckerExtendedTest {

    @Test
    @DisplayName("matchesNumericTolerance so khớp số thực với sai số epsilon")
    void testNumericTolerance() {
        assertThat(OutputChecker.matchesNumericTolerance("3.14159265", "3.14159266", 1e-6)).isTrue();
        assertThat(OutputChecker.matchesNumericTolerance("3.14", "3.15", 1e-3)).isFalse();
        assertThat(OutputChecker.matchesNumericTolerance("10 20.0000001", "10 20.0000002", 1e-5)).isTrue();
    }

    @Test
    @DisplayName("compareWithDiff phát hiện chính xác vị trí dòng sai lệch đầu tiên")
    void testDiffSummary() {
        String expected = "Line 1\nLine 2\nLine 3";
        String actual = "Line 1\nLine WRONG\nLine 3";

        OutputChecker.DiffResult diff = OutputChecker.compareWithDiff(actual, expected);
        assertThat(diff.matched()).isFalse();
        assertThat(diff.firstDiffLineIndex()).isEqualTo(2);
        assertThat(diff.expectedLine()).isEqualTo("Line 2");
        assertThat(diff.actualLine()).isEqualTo("Line WRONG");
    }

    @Test
    @DisplayName("compareWithDiff xử lý trường hợp khớp hoàn toàn")
    void testDiffMatch() {
        String expected = "Hello World\n42\n";
        String actual = "Hello World  \r\n42\n\n\n";

        OutputChecker.DiffResult diff = OutputChecker.compareWithDiff(actual, expected);
        assertThat(diff.matched()).isTrue();
    }
}
