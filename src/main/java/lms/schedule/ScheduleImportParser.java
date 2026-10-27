package lms.schedule;

import java.text.Normalizer;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lms.user.importing.AccountImportParser;

/**
 * Parse thời khóa biểu nhập hàng loạt. Java thuần (không Spring, không DB) để dễ unit test; tìm khóa học, phòng và
 * kiểm tra trùng lịch làm ở ScheduleService. Mỗi dòng 8–9 cột, dấu phân cách tự nhận như import tài khoản
 * ({@code |}, tab, {@code ;} hoặc {@code ,}):
 *
 * <pre>
 * Mã học phần | Nhóm | Thứ | Tiết bắt đầu | Số tiết | Phòng | Từ ngày | Đến ngày | Hình thức
 * INT1340|01|2|1|3|402-A2|07/09/2026|20/12/2026|LT
 * INT1339||Thứ 5|7|2|Lab 1|2026-09-07|2026-12-20|Thực hành
 * </pre>
 *
 * Thứ: 2–7, CN (hoặc 8), "Thứ 2", "T2". Ngày: dd/mm/yyyy, d/m/yyyy (hoặc - .), yyyy-mm-dd. Hình thức: LT / TH,
 * "Lý thuyết" / "Thực hành", 0 / 1; bỏ trống = lý thuyết. Nhóm bỏ trống = học phần không chia nhóm.
 */
public final class ScheduleImportParser {

    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_COURSE = "INVALID_COURSE";
    public static final String INVALID_WEEKDAY = "INVALID_WEEKDAY";
    public static final String INVALID_PERIODS = "INVALID_PERIODS";
    public static final String INVALID_ROOM = "INVALID_ROOM";
    public static final String INVALID_DATE = "INVALID_DATE";
    public static final String INVALID_RANGE = "INVALID_RANGE";
    public static final String INVALID_KIND = "INVALID_KIND";

    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final Pattern NUMBER = Pattern.compile("\\d{1,2}");
    private static final Pattern WEEKDAY = Pattern.compile("^(?:thu|t)?\\s*(\\d)$");
    private static final Pattern DAY_FIRST = Pattern.compile("(\\d{1,2})[/.-](\\d{1,2})[/.-](\\d{4})");
    private static final Pattern YEAR_FIRST = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
    private static final Set<String> HEADER_WORDS = Set.of("ma hoc phan", "ma hp", "ma mon", "ma mh", "ma mon hoc",
            "hoc phan", "course", "code");

    private final int maxLines;

    public ScheduleImportParser(int maxLines) {
        this.maxLines = maxLines;
    }

    /** weekday theo DayOfWeek (1 thứ 2 … 7 chủ nhật); group null = không chia nhóm; kind 0 lý thuyết, 1 thực hành. */
    public record Row(int line, String courseCode, String group, int weekday, int startPeriod, int periods,
            String room, LocalDate startsOn, LocalDate endsOn, int kind) {
    }

    /** label: tóm tắt dòng để admin nhận ra (vd. "INT1340 · Nhóm 01"), có thể null khi sai định dạng. */
    public record LineIssue(int line, String label, String reason) {
    }

    public record Result(List<Row> rows, List<LineIssue> errors, int dataLines) {
    }

    public static final class TooManyLinesException extends RuntimeException {
        public TooManyLinesException(int max) {
            super("Too many lines (max " + max + ")");
        }
    }

    public Result parse(String content) {
        List<Row> rows = new ArrayList<>();
        List<LineIssue> errors = new ArrayList<>();
        int dataLines = 0;
        boolean first = true;

        String text = content == null ? "" : content;
        if (text.startsWith("﻿")) {
            text = text.substring(1); // bỏ BOM khi file lưu từ Notepad/Excel
        }
        String[] lines = text.split("\\r?\\n|\\r", -1);
        for (int i = 0; i < lines.length; i++) {
            int lineNo = i + 1;
            String line = lines[i].strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            List<String> cells = trimTrailingBlanks(
                    AccountImportParser.split(line, AccountImportParser.detectDelimiter(line)).stream()
                            .map(ScheduleImportParser::clean).toList());
            boolean header = first && !cells.isEmpty() && HEADER_WORDS.contains(fold(cells.get(0)));
            first = false;
            if (header) {
                continue;
            }
            if (++dataLines > maxLines) {
                throw new TooManyLinesException(maxLines);
            }
            if (cells.size() < 8 || cells.size() > 9) {
                errors.add(new LineIssue(lineNo, cells.isEmpty() ? null : safe(cells.get(0)), INVALID_FORMAT));
                continue;
            }
            String code = cells.get(0);
            String group = cells.get(1).isEmpty() ? null : cells.get(1);
            String label = safe(code) + (group == null ? "" : " · Nhóm " + safe(group));
            if (code.isEmpty() || code.length() > 32 || (group != null && group.length() > 16)) {
                errors.add(new LineIssue(lineNo, label, INVALID_COURSE));
                continue;
            }
            Integer weekday = parseWeekday(cells.get(2));
            if (weekday == null) {
                errors.add(new LineIssue(lineNo, label, INVALID_WEEKDAY));
                continue;
            }
            Integer start = parseNumber(cells.get(3));
            Integer periods = parseNumber(cells.get(4));
            if (start == null || periods == null || start < 1 || periods < 1) {
                errors.add(new LineIssue(lineNo, label, INVALID_PERIODS));
                continue;
            }
            String room = cells.get(5);
            if (room.isEmpty() || room.length() > 32) {
                errors.add(new LineIssue(lineNo, label, INVALID_ROOM));
                continue;
            }
            LocalDate from = parseDate(cells.get(6));
            LocalDate to = parseDate(cells.get(7));
            if (from == null || to == null) {
                errors.add(new LineIssue(lineNo, label, INVALID_DATE));
                continue;
            }
            if (to.isBefore(from) || ChronoUnit.DAYS.between(from, to) >= ScheduleRules.MAX_RULE_DAYS) {
                errors.add(new LineIssue(lineNo, label, INVALID_RANGE));
                continue;
            }
            Integer kind = parseKind(cells.size() > 8 ? cells.get(8) : "");
            if (kind == null) {
                errors.add(new LineIssue(lineNo, label, INVALID_KIND));
                continue;
            }
            rows.add(new Row(lineNo, code, group, weekday, start, periods, room, from, to, kind));
        }
        return new Result(rows, errors, dataLines);
    }

    /** "2".."7", "CN"/"8" = chủ nhật; chấp nhận "Thứ 2", "thu 2", "T2". Trả về 1 thứ 2 … 7 chủ nhật. */
    static Integer parseWeekday(String value) {
        String v = fold(value).replace(" ", "");
        if (v.equals("cn") || v.equals("chunhat")) {
            return 7;
        }
        Matcher m = WEEKDAY.matcher(v);
        if (!m.matches()) {
            return null;
        }
        int n = Integer.parseInt(m.group(1));
        if (n == 8) {
            return 7;
        }
        return n >= 2 && n <= 7 ? n - 1 : null;
    }

    static Integer parseKind(String value) {
        return switch (fold(value)) {
            case "", "lt", "ly thuyet", "0" -> CourseSchedule.KIND_THEORY;
            case "th", "thuc hanh", "1" -> CourseSchedule.KIND_PRACTICE;
            default -> null;
        };
    }

    static LocalDate parseDate(String value) {
        try {
            Matcher m = DAY_FIRST.matcher(value);
            if (m.matches()) {
                return LocalDate.of(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(1)));
            }
            m = YEAR_FIRST.matcher(value);
            if (m.matches()) {
                return LocalDate.of(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                        Integer.parseInt(m.group(3)));
            }
        } catch (DateTimeException e) {
            return null;
        }
        return null;
    }

    /** Bỏ dấu, chữ thường, gộp khoảng trắng: so tiêu đề, thứ, hình thức. */
    static String fold(String value) {
        String noMarks = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return MULTI_SPACE.matcher(noMarks.replace('đ', 'd').replace('Đ', 'D').strip()).replaceAll(" ")
                .toLowerCase(Locale.ROOT);
    }

    private static Integer parseNumber(String value) {
        return NUMBER.matcher(value).matches() ? Integer.parseInt(value) : null;
    }

    private static String clean(String cell) {
        return MULTI_SPACE.matcher(Normalizer.normalize(cell, Normalizer.Form.NFC).strip()).replaceAll(" ");
    }

    private static List<String> trimTrailingBlanks(List<String> parts) {
        int end = parts.size();
        while (end > 0 && parts.get(end - 1).isEmpty()) {
            end--;
        }
        return parts.subList(0, end);
    }

    private static String safe(String value) {
        return value.length() > 40 ? value.substring(0, 40) + "…" : value;
    }
}
