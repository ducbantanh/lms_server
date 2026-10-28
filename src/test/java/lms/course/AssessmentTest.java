package lms.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class AssessmentTest {

    private static final Instant NOW = Instant.parse("2026-10-17T08:00:00Z");
    private static final Duration HOUR = Duration.ofHours(1);

    private static Assessment at(Instant opens, Instant closes) {
        return new Assessment(1L, Assessment.TYPE_PRACTICE, "Bài 1", null, null, opens, closes);
    }

    @Test
    void openImmediatelyWithoutDeadline() {
        Assessment a = at(null, null);
        assertTrue(a.isOpened(NOW));
        assertEquals("open", a.statusAt(NOW));
    }

    @Test
    void upcomingBeforeOpensAt() {
        Assessment a = at(NOW.plus(HOUR), null);
        assertFalse(a.isOpened(NOW));
        assertEquals("upcoming", a.statusAt(NOW));
    }

    @Test
    void openExactlyAtOpensAt() {
        assertEquals("open", at(NOW, NOW.plus(HOUR)).statusAt(NOW));
    }

    @Test
    void closedAfterClosesAt() {
        assertEquals("closed", at(NOW.minus(HOUR.multipliedBy(2)), NOW.minus(HOUR)).statusAt(NOW));
    }

    private static Assessment exam(Instant opens, Instant closes) {
        return new Assessment(1L, Assessment.TYPE_EXAM, "Thi", null, 1L, opens, closes);
    }

    @Test
    void finishedExamCannotBeDeleted() {
        assertFalse(exam(NOW.minus(HOUR.multipliedBy(2)), NOW.minus(HOUR)).isDeletable(NOW));
    }

    @Test
    void examBeforeOrDuringWindowCanBeDeleted() {
        assertTrue(exam(NOW.plus(HOUR), NOW.plus(HOUR.multipliedBy(2))).isDeletable(NOW));
        assertTrue(exam(NOW.minus(HOUR), NOW.plus(HOUR)).isDeletable(NOW));
        assertTrue(exam(null, null).isDeletable(NOW));
    }

    @Test
    void closedPracticeCanStillBeDeleted() {
        assertTrue(at(NOW.minus(HOUR.multipliedBy(2)), NOW.minus(HOUR)).isDeletable(NOW));
    }

    @Test
    void quizDeadlineIsEarlierOfCloseAndTimeLimit() {
        Instant closes = NOW.plus(HOUR);
        Assessment limited = Assessment.quiz(1L, Assessment.TYPE_EXAM, "Thi", null, null, closes, 45, false,
                Assessment.REVEAL_NEVER, Assessment.SCORE_IMMEDIATE);
        assertEquals(NOW.plus(Duration.ofMinutes(45)), limited.deadlineFor(NOW));
        // Bắt đầu muộn: hết giờ đóng trước khi đủ 45 phút
        assertEquals(closes, limited.deadlineFor(NOW.plus(Duration.ofMinutes(30))));
        Assessment untilClose = Assessment.quiz(1L, Assessment.TYPE_LAB, "TH", null, null, closes, null, false,
                Assessment.REVEAL_NEVER, Assessment.SCORE_IMMEDIATE);
        assertEquals(closes, untilClose.deadlineFor(NOW));
        Assessment noClose = Assessment.quiz(1L, Assessment.TYPE_LAB, "TH", null, null, null, 10, false,
                Assessment.REVEAL_NEVER, Assessment.SCORE_IMMEDIATE);
        assertEquals(NOW.plus(Duration.ofMinutes(10)), noClose.deadlineFor(NOW));
        assertEquals(null, Assessment.quiz(1L, Assessment.TYPE_LAB, "TH", null, null, null, null, false,
                Assessment.REVEAL_NEVER, Assessment.SCORE_IMMEDIATE).deadlineFor(NOW));
        assertTrue(limited.isQuiz());
    }

    @Test
    void scoreAfterCloseIsHiddenUntilClosesAt() {
        Instant closes = NOW.plus(HOUR);
        Assessment a = Assessment.quiz(1L, Assessment.TYPE_EXAM, "Thi", null, null, closes, null, false,
                Assessment.REVEAL_NEVER, Assessment.SCORE_AFTER_CLOSE);
        assertFalse(a.isScoreVisible(NOW));
        assertTrue(a.isScoreVisible(closes));
        assertTrue(at(null, closes).isScoreVisible(NOW)); // mặc định công bố ngay
        // Không có giờ đóng thì không có mốc để chờ: công bố ngay
        assertTrue(Assessment.quiz(1L, Assessment.TYPE_EXAM, "Thi", null, null, null, null, false,
                Assessment.REVEAL_NEVER, Assessment.SCORE_AFTER_CLOSE).isScoreVisible(NOW));
    }

    @Test
    void scoreNeverIsNeverVisible() {
        Assessment a = Assessment.quiz(1L, Assessment.TYPE_EXAM, "Thi", null, null, NOW, null, false,
                Assessment.REVEAL_AFTER_SUBMIT, Assessment.SCORE_NEVER);
        assertFalse(a.isScoreVisible(NOW.minus(HOUR)));
        assertFalse(a.isScoreVisible(NOW.plus(HOUR)));
    }
}
