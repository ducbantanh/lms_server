package lms.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import lms.common.RandomTokens;

class AttendanceQrTest {

    private static final String SESSION = RandomTokens.publicId();
    private static final String SECRET = RandomTokens.urlSafe(32);

    private static AttendanceQr.Parsed parse(String token) {
        return AttendanceQr.parse(token).orElseThrow();
    }

    @Test
    void periodChangesEveryTenSeconds() {
        long p = AttendanceQr.period(Instant.ofEpochSecond(1_000_000_000));
        assertEquals(p, AttendanceQr.period(Instant.ofEpochSecond(1_000_000_009)));
        assertEquals(p + 1, AttendanceQr.period(Instant.ofEpochSecond(1_000_000_010)));
        assertEquals(Instant.ofEpochSecond(1_000_000_010), AttendanceQr.periodEnd(p));
    }

    @Test
    void tokenRoundTripsAndChangesEachPeriod() {
        String token = AttendanceQr.token(SESSION, SECRET, 170_000_000L);
        AttendanceQr.Parsed parsed = parse(token);
        assertEquals(SESSION, parsed.sessionId());
        assertEquals(170_000_000L, parsed.period());
        assertNotEquals(token, AttendanceQr.token(SESSION, SECRET, 170_000_001L));
    }

    @Test
    void acceptsCurrentAndPreviousPeriodOnly() {
        long now = 170_000_000L;
        assertTrue(AttendanceQr.isValid(parse(AttendanceQr.token(SESSION, SECRET, now)), SECRET, now));
        assertTrue(AttendanceQr.isValid(parse(AttendanceQr.token(SESSION, SECRET, now - 1)), SECRET, now));
        assertFalse(AttendanceQr.isValid(parse(AttendanceQr.token(SESSION, SECRET, now - 2)), SECRET, now));
        // Mã "tương lai" (đồng hồ bị chỉnh) cũng không nhận
        assertFalse(AttendanceQr.isValid(parse(AttendanceQr.token(SESSION, SECRET, now + 1)), SECRET, now));
    }

    @Test
    void rejectsForgedOrOtherSessionCodes() {
        long now = 170_000_000L;
        assertFalse(AttendanceQr.isValid(parse(AttendanceQr.token(SESSION, RandomTokens.urlSafe(32), now)), SECRET,
                now));
        String other = RandomTokens.publicId();
        String sig = AttendanceQr.token(SESSION, SECRET, now).split("\\.")[2];
        String swapped = other + "." + Long.toString(now, 36) + "." + sig;
        assertFalse(AttendanceQr.isValid(parse(swapped), SECRET, now));
    }

    @Test
    void parseRejectsMalformedInput() {
        assertTrue(AttendanceQr.parse(null).isEmpty());
        assertTrue(AttendanceQr.parse("").isEmpty());
        assertTrue(AttendanceQr.parse("abc.def").isEmpty());
        assertTrue(AttendanceQr.parse(SESSION + ".ZZ." + SECRET.substring(0, 22)).isEmpty());
        assertTrue(AttendanceQr.parse("https://lms/attend/?t=" + SESSION).isEmpty());
    }
}
