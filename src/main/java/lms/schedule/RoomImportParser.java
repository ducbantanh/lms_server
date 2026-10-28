package lms.schedule;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import lms.user.importing.AccountImportParser;

/**
 * Parse danh sách phòng học nhập hàng loạt. Java thuần (không Spring, không DB) để dễ unit test.
 * Mỗi dòng 1–4 cột: tên phòng, tòa nhà, sức chứa, ghi chú; chỉ tên bắt buộc. Dấu phân cách tự nhận theo từng dòng
 * như import tài khoản: {@code |}, tab (dán từ Excel), {@code ;} hoặc {@code ,}.
 *
 * <pre>
 * # dòng bắt đầu bằng # và dòng trống bị bỏ qua
 * Phòng,Tòa nhà,Sức chứa,Ghi chú     ← dòng tiêu đề (cột đầu là "Phòng"/"Tên phòng"/"Room") được bỏ qua
 * 402-A2|Nhà A2|60|Có máy chiếu
 * Lab 1,Nhà A3,40
 * 105-A1
 * </pre>
 */
public final class RoomImportParser {

    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_NAME = "INVALID_NAME";
    public static final String INVALID_BUILDING = "INVALID_BUILDING";
    public static final String INVALID_CAPACITY = "INVALID_CAPACITY";
    public static final String INVALID_NOTE = "INVALID_NOTE";
    public static final String DUPLICATE_IN_INPUT = "DUPLICATE_IN_INPUT";

    public static final int MAX_CAPACITY = 2000;
    /** Chữ (có dấu), số, khoảng trắng và . - _ / ( ) #; bắt đầu bằng chữ hoặc số; tối đa 32 ký tự như rooms.name. */
    private static final Pattern NAME = Pattern.compile("^[\\p{L}\\p{N}][\\p{L}\\p{M}\\p{N} ._/()#-]{0,31}$");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final Set<String> HEADER_WORDS = Set.of("phong", "ten phong", "phong hoc", "room", "name");

    private final int maxLines;

    public RoomImportParser(int maxLines) {
        this.maxLines = maxLines;
    }

    public record Row(int line, String name, String building, Integer capacity, String note) {
    }

    public record LineIssue(int line, String name, String reason) {
    }

    public record Result(List<Row> rows, List<LineIssue> errors, List<LineIssue> duplicates, int dataLines) {
    }

    public static final class TooManyLinesException extends RuntimeException {
        public TooManyLinesException(int max) {
            super("Too many lines (max " + max + ")");
        }
    }

    public Result parse(String content) {
        List<Row> rows = new ArrayList<>();
        List<LineIssue> errors = new ArrayList<>();
        List<LineIssue> duplicates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
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
            List<String> parts = AccountImportParser.split(line, AccountImportParser.detectDelimiter(line)).stream()
                    .map(RoomImportParser::clean).toList();
            boolean header = first && HEADER_WORDS.contains(key(parts.get(0)));
            first = false;
            if (header) {
                continue;
            }
            if (++dataLines > maxLines) {
                throw new TooManyLinesException(maxLines);
            }
            // Excel thêm ô trống ở cuối khi các dòng khác có nhiều cột hơn
            List<String> cells = trimTrailingBlanks(parts);
            if (cells.isEmpty() || cells.size() > 4) {
                errors.add(new LineIssue(lineNo, null, INVALID_FORMAT));
                continue;
            }
            String name = cells.get(0);
            if (!NAME.matcher(name).matches()) {
                errors.add(new LineIssue(lineNo, safe(name), INVALID_NAME));
                continue;
            }
            String building = cells.size() > 1 ? blankToNull(cells.get(1)) : null;
            if (building != null && building.length() > 64) {
                errors.add(new LineIssue(lineNo, name, INVALID_BUILDING));
                continue;
            }
            Integer capacity = null;
            if (cells.size() > 2 && !cells.get(2).isEmpty()) {
                capacity = parseCapacity(cells.get(2));
                if (capacity == null) {
                    errors.add(new LineIssue(lineNo, name, INVALID_CAPACITY));
                    continue;
                }
            }
            String note = cells.size() > 3 ? blankToNull(cells.get(3)) : null;
            if (note != null && note.length() > 200) {
                errors.add(new LineIssue(lineNo, name, INVALID_NOTE));
                continue;
            }
            if (!seen.add(key(name))) {
                duplicates.add(new LineIssue(lineNo, name, DUPLICATE_IN_INPUT));
                continue;
            }
            rows.add(new Row(lineNo, name, building, capacity, note));
        }
        return new Result(rows, errors, duplicates, dataLines);
    }

    /**
     * Khóa so trùng trong danh sách: không phân biệt hoa thường, dấu tiếng Việt và khoảng trắng thừa, gần với cách
     * collation utf8mb4_unicode_ci của cột rooms.name so sánh (UNIQUE).
     */
    static String key(String value) {
        String noMarks = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noMarks.replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT);
    }

    /** Gộp dạng Unicode (NFC) và khoảng trắng thừa. */
    private static String clean(String cell) {
        return MULTI_SPACE.matcher(Normalizer.normalize(cell, Normalizer.Form.NFC).strip()).replaceAll(" ");
    }

    /** "60", "60 chỗ" không nhận; chỉ số nguyên 1–2000 (cho phép "1.000" kiểu Excel tiếng Việt). */
    static Integer parseCapacity(String value) {
        String digits = value.replace(".", "").replace(",", "");
        if (!digits.matches("\\d{1,5}")) {
            return null;
        }
        int n = Integer.parseInt(digits);
        return n >= 1 && n <= MAX_CAPACITY ? n : null;
    }

    private static List<String> trimTrailingBlanks(List<String> parts) {
        int end = parts.size();
        while (end > 0 && parts.get(end - 1).isEmpty()) {
            end--;
        }
        return parts.subList(0, end);
    }

    private static String blankToNull(String value) {
        return value.isEmpty() ? null : value;
    }

    private static String safe(String name) {
        return name.length() > 40 ? name.substring(0, 40) + "…" : name;
    }
}
