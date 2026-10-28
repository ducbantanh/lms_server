package lms.proctor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class ProctorRulesTest {

    @Test
    void shortAwayIsLoggedButNotCounted() {
        assertFalse(ProctorRules.counted(ProctorRules.LEFT_PAGE, 1_999L, null, true));
        assertTrue(ProctorRules.counted(ProctorRules.LEFT_PAGE, 2_000L, null, true));
        assertFalse(ProctorRules.counted(ProctorRules.LEFT_PAGE, null, null, true));
        assertFalse(ProctorRules.counted(ProctorRules.LEFT_PAGE, 60_000L, ProctorRules.FILE_PICKER, true));
        assertTrue(ProctorRules.counted(ProctorRules.LEFT_PAGE, 60_000L, "window", true));
    }

    @Test
    void fullscreenExitCountsOnlyWhenRequired() {
        assertTrue(ProctorRules.counted(ProctorRules.FULLSCREEN_EXIT, null, null, true));
        assertFalse(ProctorRules.counted(ProctorRules.FULLSCREEN_EXIT, null, null, false));
    }

    @Test
    void otherTypes() {
        assertTrue(ProctorRules.counted(ProctorRules.PASTE_BLOCKED, null, null, false));
        assertTrue(ProctorRules.counted(ProctorRules.MULTI_SCREEN, null, null, false));
        assertTrue(ProctorRules.counted(ProctorRules.IP_CHANGED, null, null, false));
        assertFalse(ProctorRules.counted(ProctorRules.COPY_BLOCKED, null, null, true));
        assertFalse(ProctorRules.counted(ProctorRules.SHORTCUT_BLOCKED, null, null, true));
        assertFalse(ProctorRules.counted(ProctorRules.RELOADED, null, null, true));
        assertFalse(ProctorRules.counted(ProctorRules.OTHER_DEVICE, null, null, true));
        assertFalse(ProctorRules.counted("made_up", 99_999L, null, true));
    }

    @Test
    void limit() {
        assertFalse(ProctorRules.reachedLimit(100, null));
        assertFalse(ProctorRules.reachedLimit(2, 3));
        assertTrue(ProctorRules.reachedLimit(3, 3));
        assertTrue(ProctorRules.reachedLimit(4, 3));
    }

    @Test
    void reportedTimeIsClampedToSession() {
        Instant from = Instant.parse("2026-10-27T01:00:00Z");
        Instant now = Instant.parse("2026-10-27T01:30:00Z");
        Instant inside = Instant.parse("2026-10-27T01:10:00Z");
        assertEquals(inside, ProctorRules.occurredAt(inside, from, now));
        assertEquals(now, ProctorRules.occurredAt(null, from, now));
        assertEquals(now, ProctorRules.occurredAt(now.plusSeconds(5), from, now));
        assertEquals(now, ProctorRules.occurredAt(from.minusSeconds(5), from, now));
    }

    @Test
    void onlineWithin45Seconds() {
        Instant now = Instant.parse("2026-10-27T01:30:00Z");
        assertTrue(ProctorRules.isOnline(now.minusSeconds(44), now));
        assertFalse(ProctorRules.isOnline(now.minusSeconds(46), now));
        assertFalse(ProctorRules.isOnline(null, now));
    }
}
