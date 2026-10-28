package lms.code;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chấm một lần nộp: ghi mã nguồn vào thư mục chấm riêng, biên dịch, chạy lần lượt từng test trong sandbox và so
 * output (OutputChecker). Không đụng tới DB; CodeJudgeWorker lấy bài từ hàng đợi và lưu kết quả.
 */
public class CodeJudge {

    private static final Logger log = LoggerFactory.getLogger(CodeJudge.class);

    public static final String AC = "AC";
    /** Sai output. */
    public static final String WA = "WA";
    /** Quá thời gian. */
    public static final String TLE = "TLE";
    /** Lỗi khi chạy (mã thoát khác 0, có thể do vượt bộ nhớ). */
    public static final String RE = "RE";
    /** Output quá lớn. */
    public static final String OLE = "OLE";
    /** Lỗi biên dịch: không chạy test nào. */
    public static final String CE = "CE";
    /** Lỗi của máy chấm (thiếu trình biên dịch, docker không chạy...): không phải lỗi của sinh viên. */
    public static final String SE = "SE";

    static final long COMPILE_TIME_MS = 30_000;
    /** Biên dịch ghi file chạy được vào thư mục: không giới hạn chặt như output của chương trình. */
    static final long COMPILE_OUTPUT_BYTES = 64L << 20;
    static final long RUN_OUTPUT_BYTES = 16L << 20;
    /** Phần thông báo lỗi biên dịch, output, stderr được lưu lại để hiển thị. */
    static final int KEEP_COMPILE_CHARS = 8000;
    static final int KEEP_OUTPUT_CHARS = 2000;

    /** Một test; nội dung đọc khi tới lượt chạy (bộ test lớn không nằm sẵn trong bộ nhớ), null = test đã bị xóa. */
    public record TestCase(boolean sample, Supplier<Content> content) {

        public static TestCase of(String input, String expected, boolean sample) {
            Content c = new Content(input, expected);
            return new TestCase(sample, () -> c);
        }
    }

    public record Content(String input, String expected) {
    }

    public record Task(CodeLanguage language, String source, List<TestCase> tests, int timeLimitMs,
            int memoryLimitMb) {
    }

    /** output chỉ giữ khi sai hoặc là test mẫu; error là stderr khi lỗi lúc chạy. */
    public record TestOutcome(String verdict, int timeMs, String output, String error) {
    }

    /**
     * verdict: AC, kết quả của test sai đầu tiên, CE hoặc SE; passed / total = số test đúng / số test của lần chạy
     * (lỗi biên dịch, lỗi máy chấm: 0 / total).
     */
    public record Result(String verdict, String compileOutput, List<TestOutcome> tests, int passed,
            int total) {
    }

    private final Sandbox sandbox;
    private final Path workRoot;

    public CodeJudge(Sandbox sandbox, Path workRoot) {
        this.sandbox = sandbox;
        this.workRoot = workRoot;
    }

    public Result judge(Task task) throws InterruptedException {
        Path dir = null;
        try {
            Files.createDirectories(workRoot);
            dir = Files.createTempDirectory(workRoot, "run-");
            Files.writeString(dir.resolve(task.language().sourceFile()), task.source(), StandardCharsets.UTF_8);
            Files.createFile(dir.resolve(Sandbox.INPUT));
            try (Sandbox.Box box = sandbox.open(dir, task.memoryLimitMb())) {
                return run(box, dir, task);
            }
        } catch (IOException e) {
            log.warn("Judge failed: {}", e.getMessage());
            return new Result(SE, "Máy chấm gặp lỗi: " + e.getMessage(), List.of(), 0, task.tests().size());
        } finally {
            if (dir != null) {
                deleteQuietly(dir);
            }
        }
    }

    private Result run(Sandbox.Box box, Path dir, Task task) throws IOException, InterruptedException {
        CodeLanguage lang = task.language();
        List<String> compile = box.compileCommand(lang);
        String compileOutput = null;
        if (compile != null) {
            Sandbox.Exec e = box.exec(compile, COMPILE_TIME_MS, null, COMPILE_OUTPUT_BYTES);
            compileOutput = blankToNull(read(dir.resolve(Sandbox.ERROR), KEEP_COMPILE_CHARS) + "\n"
                    + read(dir.resolve(Sandbox.OUTPUT), KEEP_COMPILE_CHARS));
            if (e.timedOut() || e.exitCode() != 0) {
                String message = e.timedOut() ? "Biên dịch quá " + COMPILE_TIME_MS / 1000 + " giây." : compileOutput;
                return new Result(CE, message, List.of(), 0, task.tests().size());
            }
        }
        List<String> command = box.runCommand(lang, task.memoryLimitMb());
        long timeMs = (long) task.timeLimitMs() * lang.timeFactor();
        // Java tự giới hạn bằng -Xmx (ulimit -v làm JVM không khởi động được)
        Integer memory = lang == CodeLanguage.JAVA ? null : task.memoryLimitMb();
        List<TestOutcome> outcomes = new ArrayList<>();
        String verdict = AC;
        int passed = 0;
        for (TestCase t : task.tests()) {
            Content c = t.content().get();
            if (c == null) {
                // Giảng viên vừa sửa bộ test: bài đã được đưa về hàng đợi, kết quả này sẽ bị bỏ
                throw new IOException("Test removed while judging");
            }
            Files.writeString(dir.resolve(Sandbox.INPUT), c.input(), StandardCharsets.UTF_8);
            Sandbox.Exec e = box.exec(command, timeMs, memory, RUN_OUTPUT_BYTES);
            Path out = dir.resolve(Sandbox.OUTPUT);
            String v;
            if (e.timedOut()) {
                v = TLE;
            } else if (e.outputExceeded() || Files.size(out) > RUN_OUTPUT_BYTES) {
                v = OLE;
            } else if (e.exitCode() != 0) {
                v = RE;
            } else {
                v = OutputChecker.matches(read(out, Integer.MAX_VALUE), c.expected()) ? AC : WA;
            }
            boolean ok = AC.equals(v);
            outcomes.add(new TestOutcome(v, (int) Math.min(e.timeMs(), Integer.MAX_VALUE),
                    ok && !t.sample() ? null : blankToNull(read(out, KEEP_OUTPUT_CHARS)),
                    RE.equals(v) ? blankToNull(read(dir.resolve(Sandbox.ERROR), KEEP_OUTPUT_CHARS)) : null));
            if (ok) {
                passed++;
            } else if (AC.equals(verdict)) {
                verdict = v;
            }
        }
        return new Result(verdict, compileOutput, outcomes, passed, outcomes.size());
    }

    /** Đọc tối đa maxChars ký tự đầu (UTF-8, byte lỗi được thay thế); file không có thì chuỗi rỗng. */
    private static String read(Path file, int maxChars) throws IOException {
        if (!Files.exists(file)) {
            return "";
        }
        // UTF-8 tối đa 4 byte/ký tự
        long maxBytes = Math.min(Files.size(file), maxChars == Integer.MAX_VALUE ? Long.MAX_VALUE : maxChars * 4L);
        byte[] bytes;
        try (InputStream in = Files.newInputStream(file)) {
            bytes = in.readNBytes((int) Math.min(maxBytes, Integer.MAX_VALUE - 8));
        }
        String text = new String(bytes, StandardCharsets.UTF_8);
        return text.length() > maxChars ? text.substring(0, maxChars) + "…" : text;
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.strip();
    }

    private static void deleteQuietly(Path dir) {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.debug("Cannot delete {}: {}", p, e.getMessage());
                }
            });
        } catch (IOException e) {
            log.warn("Cannot clean judge dir {}: {}", dir, e.getMessage());
        }
    }
}
