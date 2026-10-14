package lms.user.importing;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Parse nội dung import sinh viên. Java thuần (không Spring, không DB) để dễ unit test.
 *
 * <pre>
 * # dòng bắt đầu bằng # và dòng trống bị bỏ qua
 * student_id|ddmmyyyy|Full name
 * B21DCCN001|01012003|Nguyễn Văn A
 * </pre>
 */
public final class StudentImportParser {

    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String INVALID_STUDENT_ID = "INVALID_STUDENT_ID";
    public static final String INVALID_DATE = "INVALID_DATE";
    public static final String INVALID_NAME = "INVALID_NAME";
    public static final String DUPLICATE_IN_INPUT = "DUPLICATE_IN_INPUT";

    /** Bắt đầu bằng chữ/số, 3-32 ký tự gồm chữ, số, _ . - */
    private static final Pattern STUDENT_ID = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_.-]{2,31}$");
    /** Chữ cái mọi ngôn ngữ (có dấu tiếng Việt), dấu kết hợp, khoảng trắng, ' . - */
    private static final Pattern FULL_NAME = Pattern.compile("^[\\p{L}\\p{M}][\\p{L}\\p{M} .'-]*$");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final DateTimeFormatter DDMMYYYY =
            DateTimeFormatter.ofPattern("ddMMuuuu").withResolverStyle(ResolverStyle.STRICT);

    private final int maxLines;

    public StudentImportParser(int maxLines) {
        this.maxLines = maxLines;
    }

    public record Row(int line, String studentId, LocalDate birthDate, String rawBirthDate, String fullName) {
    }

    public record LineIssue(int line, String studentId, String reason) {
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

        String text = content == null ? "" : content;
        if (text.startsWith("﻿")) {
            text = text.substring(1); // bỏ BOM khi file lưu từ Notepad
        }
        String[] lines = text.split("\\r?\\n|\\r", -1);

        for (int i = 0; i < lines.length; i++) {
            int lineNo = i + 1;
            String line = lines[i].strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (++dataLines > maxLines) {
                throw new TooManyLinesException(maxLines);
            }

            String[] parts = line.split("\\|", -1);
            if (parts.length != 3) {
                errors.add(new LineIssue(lineNo, null, INVALID_FORMAT));
                continue;
            }
            String id = parts[0].strip();
            String rawDate = parts[1].strip();
            String name = MULTI_SPACE.matcher(parts[2].strip()).replaceAll(" ");

            if (!STUDENT_ID.matcher(id).matches()) {
                errors.add(new LineIssue(lineNo, safeId(id), INVALID_STUDENT_ID));
                continue;
            }
            LocalDate birth = parseDate(rawDate);
            if (birth == null) {
                errors.add(new LineIssue(lineNo, id, INVALID_DATE));
                continue;
            }
            if (name.length() < 2 || name.length() > 100 || !FULL_NAME.matcher(name).matches()) {
                errors.add(new LineIssue(lineNo, id, INVALID_NAME));
                continue;
            }

            String key = id.toUpperCase(Locale.ROOT);
            if (seen.containsKey(key)) {
                duplicates.add(new LineIssue(lineNo, id, DUPLICATE_IN_INPUT));
                continue;
            }
            seen.put(key, lineNo);
            rows.add(new Row(lineNo, id, birth, rawDate, name));
        }
        return new Result(rows, errors, duplicates, dataLines);
    }

    private static LocalDate parseDate(String raw) {
        if (raw.length() != 8) {
            return null;
        }
        try {
            LocalDate d = LocalDate.parse(raw, DDMMYYYY);
            if (d.getYear() < 1900 || d.isAfter(LocalDate.now())) {
                return null;
            }
            return d;
        } catch (DateTimeException e) {
            return null;
        }
    }

    /** Không trả lại chuỗi dài/bất thường trong báo cáo lỗi. */
    private static String safeId(String id) {
        return id.length() > 32 ? id.substring(0, 32) : id;
    }
}
