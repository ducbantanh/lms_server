package lms.user.importing;

import java.text.Normalizer;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse danh sách tài khoản cần tạo (sinh viên hoặc giảng viên). Java thuần (không Spring, không DB) để dễ unit test.
 * Mỗi dòng 3 cột: mã đăng nhập, ngày sinh, họ tên. Dấu phân cách tự nhận theo từng dòng:
 * {@code |} (file .txt), tab (dán từ Excel), {@code ;} (CSV của Excel tiếng Việt) hoặc {@code ,} (CSV).
 *
 * <pre>
 * # dòng bắt đầu bằng # và dòng trống bị bỏ qua
 * B21DCCN001|01012003|Nguyễn Văn A
 * MSSV,Ngày sinh,Họ tên          ← dòng tiêu đề CSV (cột ngày không có chữ số) được bỏ qua
 * B21DCCN002,"1/2/2003","Trần Thị B"
 * </pre>
 *
 * Ngày sinh nhận ddmmyyyy, d/m/yyyy (hoặc - .), yyyy-mm-dd, và 7 chữ số (Excel bỏ số 0 đầu của 01012003);
 * mật khẩu ban đầu luôn là ngày sinh dạng ddmmyyyy.
 */
public final class AccountImportParser {

    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_USERNAME = "INVALID_USERNAME";
    public static final String INVALID_DATE = "INVALID_DATE";
    public static final String INVALID_NAME = "INVALID_NAME";
    public static final String DUPLICATE_IN_INPUT = "DUPLICATE_IN_INPUT";

    /** Bắt đầu bằng chữ/số, 3-32 ký tự gồm chữ, số, _ . - */
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_.-]{2,31}$");
    /** Chữ cái mọi ngôn ngữ (có dấu tiếng Việt), dấu kết hợp, khoảng trắng, ' . - */
    private static final Pattern FULL_NAME = Pattern.compile("^[\\p{L}\\p{M}][\\p{L}\\p{M} .'-]*$");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final Pattern DIGITS = Pattern.compile("\\d{7,8}");
    private static final Pattern DAY_FIRST = Pattern.compile("(\\d{1,2})[/.-](\\d{1,2})[/.-](\\d{4})");
    private static final Pattern YEAR_FIRST = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})");
    private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("ddMMuuuu");

    private final int maxLines;

    public AccountImportParser(int maxLines) {
        this.maxLines = maxLines;
    }

    /** initialPassword: ngày sinh dạng ddmmyyyy. */
    public record Row(int line, String username, LocalDate birthDate, String initialPassword, String fullName) {
    }

    public record LineIssue(int line, String username, String reason) {
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
        Map<String, Integer> seen = new HashMap<>();
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
            // Nhận dấu phân cách theo từng dòng: dòng hỏng ở đầu không làm sai các dòng sau
            List<String> parts = split(line, detectDelimiter(line));
            boolean header = first && isHeader(parts);
            first = false;
            if (header) {
                continue;
            }
            if (++dataLines > maxLines) {
                throw new TooManyLinesException(maxLines);
            }
            if (parts.size() != 3) {
                errors.add(new LineIssue(lineNo, null, INVALID_FORMAT));
                continue;
            }
            String username = parts.get(0).strip();
            String name = normalizeName(parts.get(2));

            if (!isValidUsername(username)) {
                errors.add(new LineIssue(lineNo, safeUsername(username), INVALID_USERNAME));
                continue;
            }
            LocalDate birth = parseDate(parts.get(1));
            if (birth == null) {
                errors.add(new LineIssue(lineNo, username, INVALID_DATE));
                continue;
            }
            if (!isValidName(name)) {
                errors.add(new LineIssue(lineNo, username, INVALID_NAME));
                continue;
            }

            String key = username.toUpperCase(Locale.ROOT);
            if (seen.containsKey(key)) {
                duplicates.add(new LineIssue(lineNo, username, DUPLICATE_IN_INPUT));
                continue;
            }
            seen.put(key, lineNo);
            rows.add(new Row(lineNo, username, birth, birth.format(DDMMYYYY), name));
        }
        return new Result(rows, errors, duplicates, dataLines);
    }

    // ---------------------------------------------------------------- dùng chung với sửa tài khoản

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME.matcher(username).matches();
    }

    /** Gộp dạng Unicode (NFC: file CP1258 dùng dấu kết hợp) và khoảng trắng thừa. */
    public static String normalizeName(String name) {
        return MULTI_SPACE.matcher(Normalizer.normalize(name == null ? "" : name, Normalizer.Form.NFC).strip())
                .replaceAll(" ");
    }

    public static boolean isValidName(String name) {
        return name.length() >= 2 && name.length() <= 100 && FULL_NAME.matcher(name).matches();
    }

    /** Ngày sinh hợp lệ: từ năm 1900 tới hôm nay. */
    public static boolean isValidBirthDate(LocalDate date) {
        return date.getYear() >= 1900 && !date.isAfter(LocalDate.now());
    }

    /** Mật khẩu ban đầu / đặt lại: ngày sinh dạng ddmmyyyy. */
    public static String passwordFor(LocalDate birthDate) {
        return birthDate.format(DDMMYYYY);
    }

    // ---------------------------------------------------------------- nội bộ

    /** Ưu tiên | rồi tab rồi ; rồi , — chỉ xét ký tự ngoài dấu nháy (họ tên "A; B" không làm đổi dấu). */
    public static char detectDelimiter(String line) {
        StringBuilder outside = new StringBuilder();
        boolean quoted = false;
        for (char ch : line.toCharArray()) {
            if (ch == '"') {
                quoted = !quoted;
            } else if (!quoted) {
                outside.append(ch);
            }
        }
        for (char candidate : new char[] { '|', '\t', ';' }) {
            if (outside.indexOf(String.valueOf(candidate)) >= 0) {
                return candidate;
            }
        }
        return ',';
    }

    /** Tách một dòng kiểu CSV: ô bọc trong "..." được chứa dấu phân cách, "" là một dấu nháy. */
    public static List<String> split(String line, char delimiter) {
        List<String> cells = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cell.append('"');
                    i++;
                } else if (ch == '"') {
                    quoted = false;
                } else {
                    cell.append(ch);
                }
            } else if (ch == '"' && cell.toString().isBlank()) {
                quoted = true;
                cell.setLength(0);
            } else if (ch == delimiter) {
                cells.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(ch);
            }
        }
        cells.add(cell.toString());
        return cells;
    }

    /** Dòng tiêu đề: đủ 3 cột và cột ngày sinh không có chữ số nào ("Ngày sinh", "ddmmyyyy"...). */
    static boolean isHeader(List<String> parts) {
        return parts.size() == 3 && parts.get(1).chars().noneMatch(Character::isDigit);
    }

    static LocalDate parseDate(String raw) {
        String s = raw == null ? "" : raw.strip();
        int day;
        int month;
        int year;
        Matcher m;
        if (DIGITS.matcher(s).matches()) {
            String d = s.length() == 7 ? "0" + s : s;
            day = Integer.parseInt(d.substring(0, 2));
            month = Integer.parseInt(d.substring(2, 4));
            year = Integer.parseInt(d.substring(4));
        } else if ((m = DAY_FIRST.matcher(s)).matches()) {
            day = Integer.parseInt(m.group(1));
            month = Integer.parseInt(m.group(2));
            year = Integer.parseInt(m.group(3));
        } else if ((m = YEAR_FIRST.matcher(s)).matches()) {
            year = Integer.parseInt(m.group(1));
            month = Integer.parseInt(m.group(2));
            day = Integer.parseInt(m.group(3));
        } else {
            return null;
        }
        try {
            LocalDate date = LocalDate.of(year, month, day);
            return isValidBirthDate(date) ? date : null;
        } catch (DateTimeException e) {
            return null;
        }
    }

    /** Không trả lại chuỗi dài/bất thường trong báo cáo lỗi. */
    private static String safeUsername(String username) {
        return username.length() > 32 ? username.substring(0, 32) : username;
    }
}
