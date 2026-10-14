package lms.common;

/**
 * Exception nghiệp vụ. Service ném exception này, GlobalExceptionHandler đổi thành ApiResponse.
 */
public class AppException extends RuntimeException {

    private final ErrorCode code;
    private final transient Object details;

    public AppException(ErrorCode code) {
        this(code, code.defaultMessage(), null);
    }

    public AppException(ErrorCode code, String message) {
        this(code, message, null);
    }

    public AppException(ErrorCode code, String message, Object details) {
        super(message);
        this.code = code;
        this.details = details;
    }

    public ErrorCode code() {
        return code;
    }

    public Object details() {
        return details;
    }

    public static AppException notFound(String what) {
        return new AppException(ErrorCode.NOT_FOUND, what + " not found");
    }
}
