package lms.common.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeUtilsTest {

    @Test
    @DisplayName("nowVn và toVnDateTime xử lý múi giờ Việt Nam UTC+7")
    void testTimezoneConversions() {
        LocalDateTime now = DateTimeUtils.nowVn();
        assertThat(now).isNotNull();

        Instant instant = Instant.parse("2026-10-28T08:00:00Z");
        LocalDateTime vn = DateTimeUtils.toVnDateTime(instant);
        assertThat(vn.getHour()).isEqualTo(15); // 8 + 7 = 15
        assertThat(vn.getDayOfMonth()).isEqualTo(28);

        Instant backToInstant = DateTimeUtils.toInstant(vn);
        assertThat(backToInstant).isEqualTo(instant);
    }

    @Test
    @DisplayName("formatVn và formatDateOnly hiển thị đúng định dạng Việt Nam")
    void testFormatters() {
        LocalDateTime dt = LocalDateTime.of(2026, 10, 28, 14, 30);
        assertThat(DateTimeUtils.formatVn(dt)).isEqualTo("14:30 28/10/2026");

        LocalDate date = LocalDate.of(2026, 10, 28);
        assertThat(DateTimeUtils.formatDateOnly(date)).isEqualTo("28/10/2026");
    }

    @Test
    @DisplayName("formatRelativeVn sinh câu thời gian tương đối bằng tiếng Việt")
    void testRelativeVn() {
        Instant now = Instant.now();
        assertThat(DateTimeUtils.formatRelativeVn(now.minusSeconds(10))).isEqualTo("vừa xong");
        assertThat(DateTimeUtils.formatRelativeVn(now.minus(5, ChronoUnit.MINUTES))).isEqualTo("5 phút trước");
        assertThat(DateTimeUtils.formatRelativeVn(now.minus(3, ChronoUnit.HOURS))).isEqualTo("3 giờ trước");
        assertThat(DateTimeUtils.formatRelativeVn(now.minus(2, ChronoUnit.DAYS))).isEqualTo("2 ngày trước");
        assertThat(DateTimeUtils.formatRelativeVn(null)).isEqualTo("");
    }

    @Test
    @DisplayName("dayOfWeekVn chuyển đổi thứ trong tuần sang tiếng Việt")
    void testDayOfWeek() {
        assertThat(DateTimeUtils.dayOfWeekVn(DayOfWeek.MONDAY)).isEqualTo("Thứ Hai");
        assertThat(DateTimeUtils.dayOfWeekVn(DayOfWeek.SUNDAY)).isEqualTo("Chủ Nhật");
        assertThat(DateTimeUtils.dayOfWeekVn(DayOfWeek.FRIDAY)).isEqualTo("Thứ Sáu");
        assertThat(DateTimeUtils.dayOfWeekVn(null)).isEqualTo("");
    }

    @Test
    @DisplayName("isBetween kiểm tra mốc thời gian nằm trong khoảng hiệu lực")
    void testIsBetween() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(3600);
        Instant future = now.plusSeconds(3600);

        assertThat(DateTimeUtils.isBetween(now, past, future)).isTrue();
        assertThat(DateTimeUtils.isBetween(past.minusSeconds(10), past, future)).isFalse();
        assertThat(DateTimeUtils.isBetween(future.plusSeconds(10), past, future)).isFalse();
    }
}
