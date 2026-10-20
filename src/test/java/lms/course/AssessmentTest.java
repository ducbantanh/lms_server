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
}
