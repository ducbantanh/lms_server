package lms.proctor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import lms.common.RandomTokens;

class ProctorCodeTest {

    private static final String EXAM = RandomTokens.publicId();
    private static final String SECRET = RandomTokens.urlSafe(32);

    @Test
    void periodChangesEveryMinute() {
        long p = ProctorCode.period(Instant.ofEpochSecond(1_800_000_000));
        assertEquals(p, ProctorCode.period(Instant.ofEpochSecond(1_800_000_059)));
        assertEquals(p + 1, ProctorCode.period(Instant.ofEpochSecond(1_800_000_060)));
        assertEquals(Instant.ofEpochSecond(1_800_000_060), ProctorCode.periodEnd(p));
    }

    @Test
    void codeIsSixDigitsAndDependsOnPeriodAndSecret() {
        String code = ProctorCode.code(EXAM, SECRET, 30_000_000L);
        assertTrue(code.matches("\\d{6}"));
        assertEquals(code, ProctorCode.code(EXAM, SECRET, 30_000_000L));
        // Hai chu kỳ / hai khóa khác nhau trùng mã chỉ với xác suất 1 phần triệu: thử vài chu kỳ
        boolean differs = false;
        for (long p = 30_000_001L; p < 30_000_005L; p++) {
            differs |= !code.equals(ProctorCode.code(EXAM, SECRET, p));
        }
        assertTrue(differs);
        assertNotEquals(ProctorCode.code(EXAM, SECRET, 1L) + ProctorCode.code(EXAM, SECRET, 2L),
                ProctorCode.code(EXAM, RandomTokens.urlSafe(32), 1L)
                        + ProctorCode.code(EXAM, RandomTokens.urlSafe(32), 2L));
    }

    @Test
    void acceptsCurrentAndPreviousPeriodOnly() {
        long now = 30_000_000L;
        assertTrue(ProctorCode.isValid(ProctorCode.code(EXAM, SECRET, now), EXAM, SECRET, now));
        assertTrue(ProctorCode.isValid(ProctorCode.code(EXAM, SECRET, now - 1), EXAM, SECRET, now));
        String old = ProctorCode.code(EXAM, SECRET, now - 2);
        if (!old.equals(ProctorCode.code(EXAM, SECRET, now)) && !old.equals(ProctorCode.code(EXAM, SECRET, now - 1))) {
            assertFalse(ProctorCode.isValid(old, EXAM, SECRET, now));
        }
    }

    @Test
    void ignoresSpacesAndRejectsGarbage() {
        String code = ProctorCode.code(EXAM, SECRET, 5L);
        assertTrue(ProctorCode.isValid(" " + code.substring(0, 3) + " " + code.substring(3) + " ", EXAM, SECRET, 5L));
        assertFalse(ProctorCode.isValid(null, EXAM, SECRET, 5L));
        assertFalse(ProctorCode.isValid("12345", EXAM, SECRET, 5L));
        assertFalse(ProctorCode.isValid("abcdef", EXAM, SECRET, 5L));
        assertFalse(ProctorCode.isValid(code, EXAM, null, 5L));
    }
}
