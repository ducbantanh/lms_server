package lms.code;

import java.util.Arrays;
import java.util.Optional;

/**
 * Ngôn ngữ chấm được. code là mã lưu trong DB và gửi qua API (khớp lms_client/lib/code.ts).
 * timeFactor: Java (khởi động JVM) và Python chậm hơn C/C++ nên được nhân thêm thời gian mỗi test.
 * Lệnh biên dịch/chạy cụ thể do sandbox dựng (Sandbox.Toolchain).
 */
public enum CodeLanguage {
    C("c", "C", "main.c", 1),
    CPP("cpp", "C++", "main.cpp", 1),
    JAVA("java", "Java", "Main.java", 2),
    PYTHON("python", "Python 3", "main.py", 3);

    private final String code;
    private final String label;
    private final String sourceFile;
    private final int timeFactor;

    CodeLanguage(String code, String label, String sourceFile, int timeFactor) {
        this.code = code;
        this.label = label;
        this.sourceFile = sourceFile;
        this.timeFactor = timeFactor;
    }

    public String code() {
        return code;
    }

    public String label() {
        return label;
    }

    /** Tên file mã nguồn trong thư mục chấm; Java bắt buộc lớp public tên Main. */
    public String sourceFile() {
        return sourceFile;
    }

    public int timeFactor() {
        return timeFactor;
    }

    public static Optional<CodeLanguage> of(String code) {
        return Arrays.stream(values()).filter(l -> l.code.equals(code)).findFirst();
    }
}
