package lms.code.plagiarism;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Bộ phân tích từ vựng mã nguồn chuẩn hóa (Universal Code Tokenizer).
 * Hỗ trợ bóc tách mã nguồn các ngôn ngữ C, C++, Java, Python:
 * - Loại bỏ chú thích (line comment //, # và block comment /* ... * /).
 * - Chuẩn hóa định danh biến/hàm (Variable Renaming Invariance) về token đại diện 'ID'.
 * - Giữ nguyên từ khóa cấu trúc điều khiển (if, for, while, return, class, def...).
 */
public final class CodeTokenizer {

    private static final Set<String> COMMON_KEYWORDS = Set.of(
            "if", "else", "for", "while", "do", "switch", "case", "break", "continue", "return",
            "int", "long", "double", "float", "char", "void", "bool", "boolean", "string",
            "class", "struct", "public", "private", "protected", "static", "final", "const",
            "import", "include", "package", "namespace", "using", "def", "lambda", "try", "catch",
            "throw", "throws", "new", "this", "super", "null", "true", "false", "sizeof"
    );

    private CodeTokenizer() {
    }

    /**
     * Chuyển đổi mã nguồn bất kỳ thành chuỗi ký tự Token thu gọn để đối chiếu sao chép.
     */
    public static List<String> tokenizeToRepresentation(String sourceCode) {
        if (sourceCode == null || sourceCode.isBlank()) {
            return List.of();
        }

        String stripped = stripComments(sourceCode);
        List<String> tokens = new ArrayList<>();
        int len = stripped.length();
        int i = 0;

        while (i < len) {
            char c = stripped.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            // Xử lý chuỗi ký tự
            if (c == '\"' || c == '\'') {
                char quote = c;
                i++;
                while (i < len && stripped.charAt(i) != quote) {
                    if (stripped.charAt(i) == '\\' && i + 1 < len) {
                        i += 2;
                    } else {
                        i++;
                    }
                }
                if (i < len) i++; // bỏ qua dấu đóng
                tokens.add("STR");
                continue;
            }

            // Xử lý số
            if (Character.isDigit(c)) {
                while (i < len && (Character.isDigit(stripped.charAt(i)) || stripped.charAt(i) == '.')) {
                    i++;
                }
                tokens.add("NUM");
                continue;
            }

            // Xử lý từ khóa hoặc định danh
            if (Character.isLetter(c) || c == '_') {
                int start = i;
                while (i < len && (Character.isLetterOrDigit(stripped.charAt(i)) || stripped.charAt(i) == '_')) {
                    i++;
                }
                String word = stripped.substring(start, i).toLowerCase();
                if (COMMON_KEYWORDS.contains(word)) {
                    tokens.add("KW_" + word.toUpperCase());
                } else {
                    tokens.add("ID");
                }
                continue;
            }

            // Toán tử hoặc dấu phân cách
            tokens.add(String.valueOf(c));
            i++;
        }

        return tokens;
    }

    /**
     * Loại bỏ chú thích mã nguồn (Comments Removal).
     */
    public static String stripComments(String code) {
        if (code == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int len = code.length();
        int i = 0;
        boolean inBlockComment = false;
        boolean inLineComment = false;
        boolean inString = false;
        char quoteChar = 0;

        while (i < len) {
            char c = code.charAt(i);
            char next = (i + 1 < len) ? code.charAt(i + 1) : 0;

            if (inString) {
                sb.append(c);
                if (c == '\\' && i + 1 < len) {
                    sb.append(next);
                    i += 2;
                    continue;
                }
                if (c == quoteChar) {
                    inString = false;
                }
                i++;
                continue;
            }

            if (inBlockComment) {
                if (c == '*' && next == '/') {
                    inBlockComment = false;
                    i += 2;
                } else {
                    i++;
                }
                continue;
            }

            if (inLineComment) {
                if (c == '\n' || c == '\r') {
                    inLineComment = false;
                    sb.append(c);
                }
                i++;
                continue;
            }

            // Bắt đầu chuỗi
            if (c == '\"' || c == '\'') {
                inString = true;
                quoteChar = c;
                sb.append(c);
                i++;
                continue;
            }

            // Block comment /*
            if (c == '/' && next == '*') {
                inBlockComment = true;
                i += 2;
                continue;
            }

            // Line comment // hoặc # (Python)
            if ((c == '/' && next == '/') || c == '#') {
                inLineComment = true;
                i++;
                continue;
            }

            sb.append(c);
            i++;
        }

        return sb.toString();
    }
}
