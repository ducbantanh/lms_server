package lms.code;

import java.util.Arrays;
import java.util.Optional;

/**
 * Danh mục ngôn ngữ lập trình được hỗ trợ chấm tự động trên hệ thống LMS.
 * Đồng bộ mã định danh với client.
 * timeFactor: Hệ số nhân thời gian thực thi (Java, Python cần thời gian khởi động runtime).
 * memoryFactor: Hệ số nhân dung lượng bộ nhớ cho phép.
 */
public enum CodeLanguage {
    C(
            "c",
            "C (GCC 13)",
            "main.c",
            ".c",
            1,
            1,
            true,
            "#include <stdio.h>\n\nint main(void) {\n    // Nhập xuất dữ liệu chuẩn\n    printf(\"Hello, World!\\n\");\n    return 0;\n}\n"
    ),
    CPP(
            "cpp",
            "C++ (G++ 13, C++20)",
            "main.cpp",
            ".cpp",
            1,
            1,
            true,
            "#include <iostream>\n\nusing namespace std;\n\nint main() {\n    ios_base::sync_with_stdio(false);\n    cin.tie(NULL);\n    cout << \"Hello, World!\" << \"\\n\";\n    return 0;\n}\n"
    ),
    JAVA(
            "java",
            "Java (OpenJDK 17/21)",
            "Main.java",
            ".java",
            2,
            2,
            true,
            "import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        System.out.println(\"Hello, World!\");\n    }\n}\n"
    ),
    PYTHON(
            "python",
            "Python 3 (CPython 3.11+)",
            "main.py",
            ".py",
            3,
            2,
            false,
            "import sys\n\ndef main():\n    # Nhập xuất nhanh\n    print(\"Hello, World!\")\n\nif __name__ == '__main__':\n    main()\n"
    );

    private final String code;
    private final String label;
    private final String sourceFile;
    private final String extension;
    private final int timeFactor;
    private final int memoryFactor;
    private final boolean compiled;
    private final String starterTemplate;

    CodeLanguage(
            String code,
            String label,
            String sourceFile,
            String extension,
            int timeFactor,
            int memoryFactor,
            boolean compiled,
            String starterTemplate) {
        this.code = code;
        this.label = label;
        this.sourceFile = sourceFile;
        this.extension = extension;
        this.timeFactor = timeFactor;
        this.memoryFactor = memoryFactor;
        this.compiled = compiled;
        this.starterTemplate = starterTemplate;
    }

    public String code() {
        return code;
    }

    public String label() {
        return label;
    }

    /**
     * Tên file mã nguồn trong thư mục sandbox (Java bắt buộc class Main).
     */
    public String sourceFile() {
        return sourceFile;
    }

    public String extension() {
        return extension;
    }

    public int timeFactor() {
        return timeFactor;
    }

    public int memoryFactor() {
        return memoryFactor;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public boolean isInterpreted() {
        return !compiled;
    }

    public String starterTemplate() {
        return starterTemplate;
    }

    public static Optional<CodeLanguage> of(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(l -> l.code.equalsIgnoreCase(code.trim()))
                .findFirst();
    }
}
