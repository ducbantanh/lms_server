package lms.code;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lms.common.RandomTokens;

/**
 * Mỗi lần chấm một container (image app.judge.docker-image, dựng từ judge/Dockerfile) gắn thư mục chấm vào
 * /box: không mạng, giới hạn RAM, 1 CPU, tối đa 128 tiến trình, bỏ mọi capability, chạy bằng uid của chủ
 * thư mục chấm (không phải root). Mỗi lệnh chạy bằng docker exec, bị `timeout` trong container dừng khi quá
 * giờ; ulimit giới hạn dung lượng file output và bộ nhớ ảo. Container tự xóa khi xong (hoặc sau 15 phút nếu
 * server chết giữa chừng).
 */
class DockerSandbox implements Sandbox {

    private static final Logger log = LoggerFactory.getLogger(DockerSandbox.class);

    /** Các trình biên dịch có sẵn trong image (judge/Dockerfile). */
    private static final Toolchain TOOLS = new Toolchain("gcc", "g++", "javac", "java", "python3");

    /** timeout -s KILL trả 137 (128 + SIGKILL); vượt ulimit -f: 153 (128 + SIGXFSZ). */
    private static final int KILLED = 137;
    private static final int FILE_TOO_LARGE = 153;

    private final String docker;
    private final String image;

    DockerSandbox(String docker, String image) {
        this.docker = docker;
        this.image = image;
    }

    @Override
    public Box open(Path dir, int memoryMb) throws IOException {
        String name = "lms-judge-" + RandomTokens.publicId().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        List<String> cmd = new ArrayList<>(List.of(docker, "run", "-d", "--rm", "--name", name,
                "--network", "none", "--cpus", "1",
                // Đủ cho trình biên dịch C++/Java; chương trình bị giới hạn chặt hơn bằng ulimit -v hoặc -Xmx
                "--memory", Math.max(1024, memoryMb + 512) + "m", "--memory-swap", Math.max(1024, memoryMb + 512) + "m",
                "--pids-limit", "128", "--cap-drop", "ALL", "--security-opt", "no-new-privileges",
                "--user", owner(dir), "-v", dir.toAbsolutePath() + ":/box", "-w", "/box", image, "sleep", "900"));
        Result r = run(cmd, 60);
        if (r.exitCode() != 0) {
            throw new IOException("docker run failed: " + r.output());
        }
        return new DockerBox(name);
    }

    /** uid:gid của chủ thư mục chấm để file trong /box xóa được sau đó; hệ không có uid thì dùng nobody. */
    private static String owner(Path dir) {
        try {
            return Files.getAttribute(dir, "unix:uid") + ":" + Files.getAttribute(dir, "unix:gid");
        } catch (IOException | UnsupportedOperationException | IllegalArgumentException e) {
            return "65534:65534";
        }
    }

    private record Result(int exitCode, String output) {
    }

    /** Chạy lệnh docker trên máy server, gom stdout+stderr (để báo lỗi). */
    private static Result run(List<String> cmd, int timeoutSeconds) throws IOException {
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        try {
            if (!p.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return new Result(-1, "timed out");
            }
            return new Result(p.exitValue(), new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            p.destroyForcibly();
            Thread.currentThread().interrupt();
            throw new IOException("interrupted", e);
        }
    }

    private class DockerBox implements Box {

        private final String name;

        DockerBox(String name) {
            this.name = name;
        }

        @Override
        public List<String> compileCommand(CodeLanguage language) {
            return TOOLS.compile(language, "./main");
        }

        @Override
        public List<String> runCommand(CodeLanguage language, int memoryMb) {
            return TOOLS.run(language, "./main", memoryMb);
        }

        @Override
        public Exec exec(List<String> command, long timeMs, Integer memoryMb, long outputBytes)
                throws IOException, InterruptedException {
            String limits = "ulimit -f " + (outputBytes / 1024 + 1) + ";"
                    + (memoryMb == null ? "" : " ulimit -v " + memoryMb * 1024L + ";");
            List<String> cmd = new ArrayList<>(List.of(docker, "exec", name, "timeout", "-s", "KILL",
                    String.format(Locale.ROOT, "%.3f", timeMs / 1000.0), "sh", "-c",
                    limits + " exec \"$@\" <" + INPUT + " >" + OUTPUT + " 2>" + ERROR, "sh"));
            cmd.addAll(command);
            long start = System.nanoTime();
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
            // timeout trong container dừng chương trình; chờ thêm cho phần khởi động của docker exec
            if (!p.waitFor(timeMs + 15_000, TimeUnit.MILLISECONDS)) {
                p.destroyForcibly();
                return new Exec(-1, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start), true, false);
            }
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            int code = p.exitValue();
            // Bị giết sớm hơn giới hạn thời gian (vd. hết RAM của container) là lỗi khi chạy, không phải quá giờ
            boolean timedOut = (code == KILLED || code == 124) && elapsed >= timeMs;
            return new Exec(code, elapsed, timedOut, code == FILE_TOO_LARGE);
        }

        @Override
        public void close() {
            try {
                run(List.of(docker, "rm", "-f", name), 30);
            } catch (IOException e) {
                log.warn("Cannot remove judge container {}: {}", name, e.getMessage());
            }
        }
    }
}
