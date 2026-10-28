package lms.code.plagiarism;

/**
 * Đại diện cho một Token mã nguồn sau giai đoạn từ vựng học (Lexical Analysis).
 */
public record Token(TokenType type, String value, int line) {

    public enum TokenType {
        KEYWORD,
        IDENTIFIER,
        LITERAL_NUMBER,
        LITERAL_STRING,
        OPERATOR,
        DELIMITER,
        OTHER
    }
}
