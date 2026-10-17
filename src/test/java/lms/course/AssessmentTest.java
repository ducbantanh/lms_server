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
}
