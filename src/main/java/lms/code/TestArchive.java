package lms.code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * Đọc bộ test từ file ZIP (Java thuần, không đụng DB). Mỗi test là một cặp file input/output, ghép theo tên:
 * <ul>
 * <li>cùng tên, khác đuôi: {@code 1.in}/{@code 1.out}, {@code test05.inp}/{@code test05.ans}
 * (input: .in .inp .input; output: .out .ans .ok .output .sol);</li>
 * <li>file .txt có tiền tố: {@code input01.txt}/{@code output01.txt}, {@code in1.txt}/{@code out1.txt};</li>
 * <li>kiểu Themis: mỗi test một thư mục ({@code Test01/BAI.INP}, {@code Test01/BAI.OUT}).</li>
 * </ul>
 * Test sắp theo tên kiểu tự nhiên (test2 trước test10). File khác (lời giải, ghi chú), thư mục ẩn và
 * __MACOSX bị bỏ qua. Lỗi trả về theo tên test với mã lý do để giao diện hiển thị.
 */
public final class TestArchive {

    public static final String MISSING_INPUT = "MISSING_INPUT";
    public static final String MISSING_OUTPUT = "MISSING_OUTPUT";
    public static final String DUPLICATE = "DUPLICATE";
    /** Một file vượt giới hạn kích thước. */
    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    /** Tổng dung lượng giải nén vượt giới hạn. */
    public static final String TOO_LARGE = "TOO_LARGE";
    public static final String TOO_MANY = "TOO_MANY";
    public static final String NOT_ZIP = "NOT_ZIP";
    public static final String EMPTY = "EMPTY";

    /** Số mục tối đa được duyệt trong ZIP (kể cả file bị bỏ qua), chặn ZIP có hàng vạn file rỗng. */
    static final int MAX_ENTRIES = 2000;

    private static final Pattern INPUT_EXT = Pattern.compile("(?i)\\.(in|inp|input)$");
    private static final Pattern OUTPUT_EXT = Pattern.compile("(?i)\\.(out|ans|ok|output|sol)$");
    private static final Pattern INPUT_TXT = Pattern.compile("(?i)^(input|in)[-_ .]?(.*)\\.txt$");
    private static final Pattern OUTPUT_TXT = Pattern.compile("(?i)^(output|out|answer|ans|expected)[-_ .]?(.*)\\.txt$");
    private static final Pattern DIGITS = Pattern.compile("\\d+|\\D+");

    public record Limits(int maxTests, long maxFileBytes, long maxTotalBytes) {
    }

    public record Test(String name, String input, String expected) {
    }

    public record Problem(String name, String reason) {
    }

    /** ignored: số file không phải input/output (lời giải, ghi chú...). */
    public record Result(List<Test> tests, List<Problem> problems, int ignored) {

        public boolean ok() {
            return problems.isEmpty() && !tests.isEmpty();
        }
    }

    private TestArchive() {
    }

    /** Tên trong ZIP do công cụ trên Windows tạo có thể không phải UTF-8: thử lại bằng CP437. */
    public static Result read(byte[] zip, Limits limits) {
        try {
            return read(new ByteArrayInputStream(zip), StandardCharsets.UTF_8, limits);
        } catch (IllegalArgumentException malformedName) {
            return read(new ByteArrayInputStream(zip), Charset.forName("IBM437"), limits);
        }
    }

    private static Result read(InputStream in, Charset names, Limits limits) {
        Map<String, Pair> pairs = new TreeMap<>(TestArchive::natural);
        List<Problem> problems = new ArrayList<>();
        int ignored = 0;
        int entries = 0;
        long total = 0;
        try (ZipInputStream zip = new ZipInputStream(in, names)) {
            ZipEntry e;
            while ((e = zip.getNextEntry()) != null) {
                if (++entries > MAX_ENTRIES) {
                    return failed(TOO_MANY);
                }
                if (e.isDirectory() || skipped(e.getName())) {
                    continue;
                }
                Slot slot = slot(e.getName());
                if (slot == null) {
                    ignored++;
                    continue;
                }
                byte[] data = readLimited(zip, limits.maxFileBytes());
                if (data == null) {
                    problems.add(new Problem(slot.key(), FILE_TOO_LARGE));
                    continue;
                }
                total += data.length;
                if (total > limits.maxTotalBytes()) {
                    return failed(TOO_LARGE);
                }
                Pair p = pairs.computeIfAbsent(slot.key(), k -> new Pair());
                String text = text(data);
                if (slot.input() ? p.input != null : p.expected != null) {
                    problems.add(new Problem(slot.key(), DUPLICATE));
                } else if (slot.input()) {
                    p.input = text;
                } else {
                    p.expected = text;
                }
            }
        } catch (ZipException e) {
            return failed(NOT_ZIP);
        } catch (IOException e) {
            return failed(NOT_ZIP);
        }
        if (entries == 0) {
            return failed(NOT_ZIP);
        }
        List<Test> tests = new ArrayList<>();
        for (Map.Entry<String, Pair> en : pairs.entrySet()) {
            Pair p = en.getValue();
            if (p.input == null) {
                problems.add(new Problem(en.getKey(), MISSING_INPUT));
            } else if (p.expected == null) {
                problems.add(new Problem(en.getKey(), MISSING_OUTPUT));
            } else {
                tests.add(new Test(en.getKey(), p.input, p.expected));
            }
        }
        // Đổi khóa ghép cặp sang tên hiển thị
        Map<String, String> display = displayNames(pairs.keySet());
        tests.replaceAll(t -> new Test(display.get(t.name()), t.input(), t.expected()));
        problems.replaceAll(x -> new Problem(display.getOrDefault(x.name(), x.name()), x.reason()));
        if (tests.size() > limits.maxTests()) {
            problems.add(new Problem(null, TOO_MANY));
        } else if (tests.isEmpty() && problems.isEmpty()) {
            problems.add(new Problem(null, EMPTY));
        }
        return new Result(tests, problems, ignored);
    }

    private static Result failed(String reason) {
        return new Result(List.of(), List.of(new Problem(null, reason)), 0);
    }

    private static final class Pair {
        String input;
        String expected;
    }

    /** key: đường dẫn (chữ thường, bỏ đuôi / tiền tố input-output) dùng để ghép cặp. */
    private record Slot(String key, boolean input) {
    }

    static Slot slot(String path) {
        String p = path.replace('\\', '/');
        int slash = p.lastIndexOf('/');
        String dir = slash < 0 ? "" : p.substring(0, slash + 1);
        String file = p.substring(slash + 1);
        Matcher m;
        if ((m = INPUT_TXT.matcher(file)).matches()) {
            return new Slot(key(dir, m.group(2)), true);
        }
        if ((m = OUTPUT_TXT.matcher(file)).matches()) {
            return new Slot(key(dir, m.group(2)), false);
        }
        if ((m = INPUT_EXT.matcher(file)).find()) {
            return new Slot(key(dir, file.substring(0, m.start())), true);
        }
        if ((m = OUTPUT_EXT.matcher(file)).find()) {
            return new Slot(key(dir, file.substring(0, m.start())), false);
        }
        return null;
    }

    private static String key(String dir, String stem) {
        String k = (dir + stem).toLowerCase(Locale.ROOT);
        return k.endsWith("/") ? k.substring(0, k.length() - 1) : k;
    }

    private static boolean skipped(String path) {
        for (String part : path.replace('\\', '/').split("/")) {
            if (part.startsWith(".") || part.equals("__MACOSX")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tên hiển thị: bỏ thư mục chung của mọi test (ZIP thường bọc cả bộ test trong một thư mục);
     * kiểu Themis (Test01/bai) thì giữ tên thư mục.
     */
    static Map<String, String> displayNames(Iterable<String> keys) {
        List<String> list = new ArrayList<>();
        keys.forEach(list::add);
        String prefix = list.isEmpty() ? "" : commonDir(list);
        Map<String, String> out = new LinkedHashMap<>();
        for (String k : list) {
            String rest = k.substring(prefix.length());
            int slash = rest.lastIndexOf('/');
            // Test01/bai: tên test là thư mục
            String name = slash > 0 ? rest.substring(0, slash) + (rest.substring(slash + 1).isEmpty() ? ""
                    : "/" + rest.substring(slash + 1)) : rest;
            out.put(k, name.isEmpty() ? k : name.length() > 100 ? name.substring(name.length() - 100) : name);
        }
        return out;
    }

    private static String commonDir(List<String> keys) {
        String first = keys.get(0);
        int end = first.lastIndexOf('/') + 1;
        String prefix = first.substring(0, end);
        for (String k : keys) {
            while (!prefix.isEmpty() && !k.startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.substring(0, prefix.length() - 1).lastIndexOf('/') + 1);
            }
        }
        return prefix;
    }

    /** Đọc tới hết mục hiện tại; quá max byte thì bỏ phần còn lại và trả null. */
    private static byte[] readLimited(InputStream in, long max) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[64 * 1024];
        long n = 0;
        int r;
        while ((r = in.read(buf)) > 0) {
            n += r;
            if (n > max) {
                in.transferTo(OutputStream.nullOutputStream());
                return null;
            }
            out.write(buf, 0, r);
        }
        return out.toByteArray();
    }

    /** UTF-8, bỏ BOM; byte lỗi được thay thế. */
    private static String text(byte[] data) {
        int start = data.length >= 3 && (data[0] & 0xFF) == 0xEF && (data[1] & 0xFF) == 0xBB
                && (data[2] & 0xFF) == 0xBF ? 3 : 0;
        return new String(data, start, data.length - start, StandardCharsets.UTF_8);
    }

    /** So tên kiểu tự nhiên: các đoạn số so theo giá trị (test2 < test10). */
    static int natural(String a, String b) {
        Matcher ma = DIGITS.matcher(a);
        Matcher mb = DIGITS.matcher(b);
        while (ma.find() && mb.find()) {
            String x = ma.group();
            String y = mb.group();
            int c;
            if (Character.isDigit(x.charAt(0)) && Character.isDigit(y.charAt(0))) {
                c = Comparator.<String>comparingInt(s -> s.replaceFirst("^0+(?=.)", "").length())
                        .thenComparing(s -> s.replaceFirst("^0+(?=.)", "")).thenComparing(Comparator.naturalOrder())
                        .compare(x, y);
            } else {
                c = x.compareTo(y);
            }
            if (c != 0) {
                return c;
            }
        }
        return a.length() == b.length() ? a.compareTo(b) : Integer.compare(a.length(), b.length());
    }
}
