package lms.code;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OutputCheckerTest {

    @Test
    void ignoresLineEndingsTrailingSpacesAndBlankLinesAtEnd() {
        assertTrue(OutputChecker.matches("1 2\r\n3  \r\n\r\n", "1 2\n3"));
        assertTrue(OutputChecker.matches("abc", "abc\n"));
        assertTrue(OutputChecker.matches("", "\n\n"));
    }

    @Test
    void spacingInsideLinesAndLeadingBlankLinesMatter() {
        assertFalse(OutputChecker.matches("1  2", "1 2"));
        assertFalse(OutputChecker.matches(" 1", "1"));
        assertFalse(OutputChecker.matches("\n1", "1"));
        assertFalse(OutputChecker.matches("1\n\n2", "1\n2"));
    }
}
