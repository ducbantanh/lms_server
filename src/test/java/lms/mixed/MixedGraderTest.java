package lms.mixed;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import lms.common.AppException;
import lms.course.Assessment;
import lms.submission.Submission;

class MixedGraderTest {

    private static BigDecimal d(String value) {
        return new BigDecimal(value);
    }

    @Test
    void partPointsAreProportionalAndRounded() {
        assertEquals(d("4.00"), MixedGrader.points(d("4"), 10, 10));
        assertEquals(d("2.00"), MixedGrader.points(d("4"), 5, 10));
        // 2 / 3 câu AC của phần 5 điểm = 3,333… → 3,33
        assertEquals(d("3.33"), MixedGrader.points(d("5"), 2, 3));
        assertEquals(d("0.00"), MixedGrader.points(d("5"), 0, 3));
        // Phần không có câu nào: 0, không chia cho 0
        assertEquals(d("0.00"), MixedGrader.points(d("5"), 0, 0));
    }

    @Test
    void weightsNeedTwoPositivePartsAddingUpToTen() {
        assertDoesNotThrow(() -> MixedGrader.requireWeights(d("4"), d("4"), d("2")));
        assertDoesNotThrow(() -> MixedGrader.requireWeights(d("6.5"), null, d("3.50")));
        // Một phần thì là bài thường
        assertThrows(AppException.class, () -> MixedGrader.requireWeights(d("10"), null, null));
        assertThrows(AppException.class, () -> MixedGrader.requireWeights(d("4"), d("4"), null));
        assertThrows(AppException.class, () -> MixedGrader.requireWeights(d("10"), d("0"), null));
        assertThrows(AppException.class, () -> MixedGrader.requireWeights(d("5.005"), d("4.995"), null));
    }

    @Test
    void scoreWaitsForTheFilePartWhenThereIsOne() {
        Instant at = Instant.parse("2026-10-31T08:00:00Z");
        Submission s = new Submission(1L, 2L);
        s.mixedAutoGrade(d("6.00"), true, at);
        assertEquals(d("6.00"), s.getAutoScore());
        assertNull(s.getScore());

        s.gradeManualPart(d("1.50"), "Tốt", 9L, at);
        assertEquals(d("7.50"), s.getScore());
        // Chấm lại lần nộp code sau khi giảng viên chấm: điểm bài đổi theo
        s.mixedAutoGrade(d("7.00"), true, at);
        assertEquals(d("8.50"), s.getScore());
        assertEquals(9L, s.getGradedBy());
    }

    @Test
    void partsFollowTheWeights() {
        Assessment a = Assessment.mixed(1L, Assessment.TYPE_EXAM, "Giữa kỳ", null, null, null, null, 60, true,
                Assessment.REVEAL_AFTER_CLOSE, Assessment.SCORE_IMMEDIATE, d("4"), null, d("6"));
        assertTrue(a.isMixed());
        assertTrue(a.usesQuiz());
        assertFalse(a.usesCode());
        assertTrue(a.usesFiles());
        assertTrue(a.isTimed());
    }

    @Test
    void inProgressUntilSubmittedOrTimeIsUp() {
        Instant start = Instant.parse("2026-10-31T08:00:00Z");
        Assessment a = Assessment.mixed(1L, Assessment.TYPE_EXAM, "Giữa kỳ", null, null, null, null, 60, false,
                Assessment.REVEAL_NEVER, Assessment.SCORE_IMMEDIATE, null, d("5"), d("5"));
        Submission s = new Submission(1L, 2L);
        assertFalse(MixedLifecycle.isInProgress(a, s, start));
        s.start(start);
        assertTrue(MixedLifecycle.isInProgress(a, s, start.plus(Duration.ofMinutes(59))));
        assertFalse(MixedLifecycle.isInProgress(a, s, start.plus(Duration.ofMinutes(60))));
        s.submit(start.plus(Duration.ofMinutes(10)));
        assertFalse(MixedLifecycle.isInProgress(a, s, start.plus(Duration.ofMinutes(20))));
    }

    @Test
    void scoreIsTheAutoScoreWithoutAFilePart() {
        Instant at = Instant.parse("2026-10-31T08:00:00Z");
        Submission s = new Submission(1L, 2L);
        s.mixedAutoGrade(d("4.25"), false, at);
        assertEquals(d("4.25"), s.getScore());
        assertEquals(at, s.getGradedAt());
        assertNull(s.getGradedBy());
    }
}
