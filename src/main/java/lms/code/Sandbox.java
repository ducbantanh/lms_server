package lms.code;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Nơi biên dịch và chạy mã nguồn của sinh viên (LocalSandbox, DockerSandbox; chọn bằng app.judge.sandbox).
 * Mỗi lần chấm mở một Box trên thư mục chấm riêng chứa mã nguồn; mọi lệnh chạy với thư mục đó là thư mục
 * làm việc, stdin đọc từ INPUT, stdout/stderr ghi ra OUTPUT/ERROR trong thư mục.
 */
public interface Sandbox {

    String INPUT = "input.txt";
    String OUTPUT = "output.txt";
    String ERROR = "error.txt";

    /** memoryMb: giới hạn bộ nhớ lớn nhất sẽ dùng trong lần chấm (để cấp tài nguyên cho container). */
    Box open(Path dir, int memoryMb) throws IOException;

    interface Box extends AutoCloseable {

        /** Lệnh biên dịch; null = ngôn ngữ không cần biên dịch. */
        List<String> compileCommand(CodeLanguage language);

        List<String> runCommand(CodeLanguage language, int memoryMb);

        /**
         * Chạy một lệnh: quá timeMs thì bị dừng (timedOut), stdout/stderr quá outputBytes thì bị dừng
         * (outputExceeded). memoryMb null = không giới hạn bộ nhớ ảo (Java tự giới hạn bằng -Xmx).
         */
        Exec exec(List<String> command, long timeMs, Integer memoryMb, long outputBytes)
                throws IOException, InterruptedException;

        @Override
        void close();
    }

    record Exec(int exitCode, long timeMs, boolean timedOut, boolean outputExceeded) {
    }

    /** Tên trình biên dịch/thông dịch; binary là đường dẫn file chạy được mà C/C++ biên dịch ra. */
    record Toolchain(String gcc, String gpp, String javac, String java, String python) {

        public List<String> compile(CodeLanguage language, String binary) {
            return switch (language) {
                case C -> List.of(gcc, "-O2", "-std=gnu11", "-pipe", "-o", binary, language.sourceFile(), "-lm");
                case CPP -> List.of(gpp, "-O2", "-std=gnu++17", "-pipe", "-o", binary, language.sourceFile());
                case JAVA -> List.of(javac, "-encoding", "UTF-8", "-J-Xmx64m", language.sourceFile());
                // Chỉ kiểm tra cú pháp (không ghi file .pyc): lỗi cú pháp báo như lỗi biên dịch
                case PYTHON -> List.of(python, "-I", "-X", "utf8", "-c",
                        "compile(open('main.py','rb').read(),'main.py','exec')");
            };
        }

        public List<String> run(CodeLanguage language, String binary, int memoryMb) {
            return switch (language) {
                case C, CPP -> List.of(binary);
                case JAVA -> List.of(java, "-Xmx" + memoryMb + "m", "-Xss8m", "-XX:+UseSerialGC",
                        "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-cp", ".",
                        "Main");
                case PYTHON -> List.of(python, "-I", "-X", "utf8", language.sourceFile());
            };
        }
    }
}
