package lms.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class ScheduleRulesTest {

    private final ScheduleRules rules = new ScheduleRules(7, 14);

    @Test
    void weeklyDatesStartFromFirstMatchingWeekdayAndIncludeEndDate() {
        // 01/09/2026 là thứ 3; thứ 5 đầu tiên là 03/09, ngày cuối 17/09 cũng là thứ 5
        List<LocalDate> dates = ScheduleRules.weeklyDates(4, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 17));

        assertEquals(List.of(LocalDate.of(2026, 9, 3), LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 17)), dates);
    }

    @Test
    void weeklyDatesEmptyWhenRangeHasNoSuchWeekday() {
        // 01/09 (thứ 3) – 02/09 (thứ 4): không có chủ nhật
        assertTrue(ScheduleRules.weeklyDates(7, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2)).isEmpty());
    }

    @Test
    void overlapsOnlyWhenSameDayAndSharingAPeriod() {
        LocalDate d = LocalDate.of(2026, 10, 27);
        // tiết 1–3 và tiết 3–4 chung tiết 3
        assertTrue(ScheduleRules.overlaps(d, 1, 3, d, 3, 2));
        // tiết 1–3 và tiết 4–5 nối tiếp, không trùng
        assertFalse(ScheduleRules.overlaps(d, 1, 3, d, 4, 2));
        assertFalse(ScheduleRules.overlaps(d, 4, 2, d, 1, 3));
        // buổi dài bao trọn buổi ngắn
        assertTrue(ScheduleRules.overlaps(d, 1, 5, d, 2, 1));
        assertFalse(ScheduleRules.overlaps(d, 1, 3, d.plusDays(7), 1, 3));
    }

    @Test
    void slotMustFitInTheSchoolDay() {
        assertTrue(rules.isValidSlot(1, 1));
        assertTrue(rules.isValidSlot(12, 3));
        assertFalse(rules.isValidSlot(13, 3));
        assertFalse(rules.isValidSlot(0, 2));
        assertFalse(rules.isValidSlot(1, 0));
    }

    @Test
    void periodsAreOneHourStartingAtFirstHourInSchoolTimeZone() {
        LocalDate d = LocalDate.of(2026, 10, 27);
        // tiết 1 lúc 07:00 giờ Việt Nam = 00:00 UTC
        assertEquals(Instant.parse("2026-10-27T00:00:00Z"), rules.startsAt(d, 1));
        // tiết 3–5: 09:00 – 12:00
        assertEquals(Instant.parse("2026-10-27T02:00:00Z"), rules.startsAt(d, 3));
        assertEquals(Instant.parse("2026-10-27T05:00:00Z"), rules.endsAt(d, 3, 3));
    }

    @Test
    void lastPeriodMayEndAtMidnight() {
        ScheduleRules late = new ScheduleRules(10, 14);
        assertEquals(Instant.parse("2026-10-27T17:00:00Z"), late.endsAt(LocalDate.of(2026, 10, 27), 14, 1));
    }

    @Test
    void rejectsConfigurationPastMidnight() {
        assertThrows(IllegalArgumentException.class, () -> new ScheduleRules(12, 14));
    }

    @Test
    void periodLabelAndMeetingMove() {
        assertEquals("tiết 4", ScheduleService.periodLabel(4, 1));
        assertEquals("tiết 1–3", ScheduleService.periodLabel(1, 3));

        ClassMeeting m = new ClassMeeting(1L, 2L, 3L, LocalDate.of(2026, 10, 27), 1, 3, 0);
        // đổi phòng thôi: không tính là dời
        m.moveTo(LocalDate.of(2026, 10, 27), 1, 3, 4L);
        assertEquals(null, m.getOriginalDate());
        m.moveTo(LocalDate.of(2026, 10, 29), 7, 3, 4L);
        m.moveTo(LocalDate.of(2026, 10, 30), 7, 3, 4L);
        // giữ lịch ban đầu từ lần dời đầu tiên
        assertEquals(LocalDate.of(2026, 10, 27), m.getOriginalDate());
        assertEquals(1, m.getOriginalStartPeriod());
        assertEquals(9, m.endPeriod());
    }
}
