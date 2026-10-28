package lms.common.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Tiện ích thời gian chuẩn hóa theo múi giờ Việt Nam (Asia/Ho_Chi_Minh - UTC+7).
 * Cung cấp định dạng thời gian tương đối, lịch học và tính toán hạn chót nộp bài.
 */
public final class DateTimeUtils {

    public static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public static final DateTimeFormatter ISO_LOCAL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter VN_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter VN_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private DateTimeUtils() {
    }

    /**
     * Lấy thời điểm hiện tại theo múi giờ Việt Nam.
     */
    public static LocalDateTime nowVn() {
        return LocalDateTime.now(VN_ZONE);
    }

    /**
     * Chuyển đổi từ {@link Instant} sang {@link LocalDateTime} theo múi giờ Việt Nam.
     */
    public static LocalDateTime toVnDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, VN_ZONE);
    }

    /**
     * Chuyển đổi từ {@link LocalDateTime} theo múi giờ Việt Nam sang {@link Instant}.
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(VN_ZONE).toInstant();
    }

    /**
     * Định dạng ngày giờ dạng hiển thị thân thiện tại Việt Nam (Ví dụ: "14:30 28/10/2026").
     */
    public static String formatVn(LocalDateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.format(VN_DATETIME_FORMATTER);
    }

    public static String formatVn(Instant instant) {
        if (instant == null) {
            return "";
        }
        return formatVn(toVnDateTime(instant));
    }

    /**
     * Định dạng ngày Việt Nam (dd/MM/yyyy).
     */
    public static String formatDateOnly(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(VN_DATE_FORMATTER);
    }

    /**
     * Sinh chuỗi thời gian tương đối bằng tiếng Việt (Ví dụ: "vừa xong", "5 phút trước", "2 ngày trước").
     */
    public static String formatRelativeVn(Instant past) {
        if (past == null) {
            return "";
        }
        Instant now = Instant.now();
        Duration duration = Duration.between(past, now);
        long seconds = duration.getSeconds();

        if (seconds < 0) {
            return "trong tương lai";
        }
        if (seconds < 60) {
            return "vừa xong";
        }
        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " phút trước";
        }
        long hours = duration.toHours();
        if (hours < 24) {
            return hours + " giờ trước";
        }
        long days = duration.toDays();
        if (days < 30) {
            return days + " ngày trước";
        }
        long months = days / 30;
        if (months < 12) {
            return months + " tháng trước";
        }
        return (months / 12) + " năm trước";
    }

    /**
     * Tên thứ trong tuần bằng tiếng Việt (Thứ 2, Thứ 3, ..., Chủ Nhật).
     */
    public static String dayOfWeekVn(DayOfWeek day) {
        if (day == null) {
            return "";
        }
        return switch (day) {
            case MONDAY -> "Thứ Hai";
            case TUESDAY -> "Thứ Ba";
            case WEDNESDAY -> "Thứ Tư";
            case THURSDAY -> "Thứ Năm";
            case FRIDAY -> "Thứ Sáu";
            case SATURDAY -> "Thứ Bảy";
            case SUNDAY -> "Chủ Nhật";
        };
    }

    /**
     * Tính số phút chênh lệch giữa hai mốc thời gian.
     */
    public static long minutesBetween(Instant start, Instant end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Kiểm tra thời điểm hiện tại có nằm trong khoảng thời gian hiệu lực không.
     */
    public static boolean isBetween(Instant current, Instant start, Instant end) {
        if (current == null) {
            return false;
        }
        if (start != null && current.isBefore(start)) {
            return false;
        }
        if (end != null && current.isAfter(end)) {
            return false;
        }
        return true;
    }
}
