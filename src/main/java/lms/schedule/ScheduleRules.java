package lms.schedule;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Phép tính lịch không cần DB: sinh ngày theo thứ trong tuần, chồng tiết, đổi tiết ra giờ.
 * 1 tiết = 1 giờ; tiết 1 bắt đầu lúc firstHour giờ, mỗi ngày có periodsPerDay tiết.
 */
public record ScheduleRules(int firstHour, int periodsPerDay) {

    /** Lịch theo giờ của trường; DB lưu ngày + tiết, API trả thêm giờ dạng Instant. */
    public static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    /** Một quy tắc tối đa một năm học, tránh sinh hàng nghìn buổi vì gõ nhầm năm. */
    public static final int MAX_RULE_DAYS = 366;

    public ScheduleRules {
        if (firstHour < 0 || periodsPerDay < 1 || firstHour + periodsPerDay > 24) {
            throw new IllegalArgumentException("Invalid period configuration");
        }
    }

    /** Các ngày có thứ = weekday (1 thứ 2 … 7 chủ nhật) trong [from, to]. */
    public static List<LocalDate> weeklyDates(int weekday, LocalDate from, LocalDate to) {
        List<LocalDate> result = new ArrayList<>();
        for (LocalDate d = from.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(weekday))); !d.isAfter(to);
                d = d.plusWeeks(1)) {
            result.add(d);
        }
        return result;
    }

    /** Hai buổi cùng ngày có chung ít nhất một tiết. */
    public static boolean overlaps(LocalDate dateA, int startA, int periodsA, LocalDate dateB, int startB,
            int periodsB) {
        return dateA.equals(dateB) && startA < startB + periodsB && startB < startA + periodsA;
    }

    /** Tiết bắt đầu và số tiết nằm trong ngày học. */
    public boolean isValidSlot(int startPeriod, int periods) {
        return startPeriod >= 1 && periods >= 1 && startPeriod + periods - 1 <= periodsPerDay;
    }

    public Instant startsAt(LocalDate date, int startPeriod) {
        // plusHours thay vì atTime: giờ kết thúc của tiết cuối có thể là 24:00
        return date.atStartOfDay().plusHours(firstHour + startPeriod - 1L).atZone(ZONE).toInstant();
    }

    public Instant endsAt(LocalDate date, int startPeriod, int periods) {
        return startsAt(date, startPeriod + periods);
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }
}
