package lms.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Chạy thẳng trên máy server, không cô lập: chỉ giới hạn thời gian, dung lượng output, bộ nhớ ảo (ulimit, trừ
 * Windows) và xóa biến môi trường (không lộ mật khẩu DB trong .env đã nạp). Chương trình của sinh viên vẫn
 * đọc/ghi được file mà tài khoản chạy server đọc/ghi được: chỉ dùng khi phát triển hoặc thử nghiệm.
 */
class LocalSandbox implements Sandbox {

    static final boolean WINDOWS = System.getProperty("os.name", "").toLowerCase().startsWith("windows");

    /** Biến môi trường giữ lại để trình biên dịch chạy được (Windows cần SystemRoot). */
    private static final List<String> KEEP_ENV = List.of("PATH", "PATHEXT", "SystemRoot", "windir",
            "NUMBER_OF_PROCESSORS", "PROCESSOR_ARCHITECTURE", "OS");

    private final Toolchain tools;

    LocalSandbox(Toolchain tools) {
        this.tools = tools;
    }

    @Override
    public Box open(Path dir, int memoryMb) {
        return new LocalBox(dir.toAbsolutePath());
    }

    private class LocalBox implements Box {

        private final Path dir;
        private final String binary;

        LocalBox(Path dir) {
            this.dir = dir;
            // Đường dẫn tuyệt đối: Windows không tìm file chạy trong thư mục làm việc của tiến trình con
            this.binary = dir.resolve(WINDOWS ? "main.exe" : "main").toString();
        }

        @Override
        public List<String> compileCommand(CodeLanguage language) {
            return tools.compile(language, binary);
        }

        @Override
        public List<String> runCommand(CodeLanguage language, int memoryMb) {
            return tools.run(language, binary, memoryMb);
        }

        @Override
        public Exec exec(List<String> command, long timeMs, Integer memoryMb, long outputBytes)
                throws IOException, InterruptedException {
            List<String> argv = command;
            if (memoryMb != null && !WINDOWS) {
                // Thêm headroom 512 MB bộ nhớ ảo (address space) cho runtime/shared libraries (glibc, Python 3)
                // trên Linux 64-bit; tránh việc ulimit -v quá chặt làm tiến trình crash ngay khi vừa khởi động.
                long virtualLimitKb = (memoryMb + 512L) * 1024L;
                argv = new ArrayList<>(List.of("sh", "-c", "ulimit -v " + virtualLimitKb + "; exec \"$@\"", "sh"));
                argv.addAll(command);
            }
            Path input = dir.resolve(INPUT);
            if (!Files.exists(input)) {
                Files.createFile(input);
            }
            Path out = dir.resolve(OUTPUT);
            Path err = dir.resolve(ERROR);
            ProcessBuilder pb = new ProcessBuilder(argv).directory(dir.toFile())
                    .redirectInput(input.toFile())
                    .redirectOutput(out.toFile())
                    .redirectError(err.toFile());
            Map<String, String> env = pb.environment();
            Map<String, String> parent = System.getenv();
            env.clear();
            for (String key : KEEP_ENV) {
                parent.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(key)).findFirst()
                        .ifPresent(e -> env.put(e.getKey(), e.getValue()));
            }
            for (String key : List.of("TEMP", "TMP", "TMPDIR", "HOME")) {
                env.put(key, dir.toString());
            }
            if (!WINDOWS) {
                env.put("LANG", "C.UTF-8");
                env.put("LC_ALL", "C.UTF-8");
                env.put("PYTHONIOENCODING", "utf-8");
            }

            long start = System.nanoTime();
            long deadline = start + TimeUnit.MILLISECONDS.toNanos(timeMs);
            Process p = pb.start();
            boolean timedOut = false;
            boolean outputExceeded = false;
            try {
                while (!p.waitFor(10, TimeUnit.MILLISECONDS)) {
                    if (System.nanoTime() >= deadline) {
                        timedOut = true;
                        break;
                    }
                    if (Files.size(out) > outputBytes || Files.size(err) > outputBytes) {
                        outputExceeded = true;
                        break;
                    }
                }
            } finally {
                if (p.isAlive()) {
                    p.descendants().forEach(ProcessHandle::destroyForcibly);
                    p.destroyForcibly();
                    p.waitFor(5, TimeUnit.SECONDS);
                }
            }
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            outputExceeded |= Files.size(out) > outputBytes || Files.size(err) > outputBytes;
            return new Exec(p.isAlive() ? -1 : p.exitValue(), elapsed, timedOut, outputExceeded);
        }

        @Override
        public void close() {
            // Không có tài nguyên riêng; CodeJudge xóa thư mục chấm
        }
    }
}
