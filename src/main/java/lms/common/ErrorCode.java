package lms.common;

import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ ổn định. Frontend dựa vào "code" để hiển thị thông báo,
 * KHÔNG dựa vào message (message chỉ để debug).
 */
public enum ErrorCode {
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Login required"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Account is disabled"),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "Too many attempts, try again later"),
    PASSWORD_CHANGE_REQUIRED(HttpStatus.FORBIDDEN, "Password change required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Permission denied"),
    CSRF_INVALID(HttpStatus.FORBIDDEN, "Invalid CSRF token"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Invalid request"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed"),
    CONFLICT(HttpStatus.CONFLICT, "Conflict"),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "Payload too large"),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "Unsupported file type"),
    INVALID_ENROLL_KEY(HttpStatus.BAD_REQUEST, "Invalid enroll key"),
    /** Nội dung, bài, chấm điểm, thông báo: chỉ giảng viên phụ trách; admin chỉ xem. */
    TEACHER_ONLY(HttpStatus.FORBIDDEN, "Only the course teacher can do this"),
    /** Giảng viên được chọn không tồn tại, không phải giảng viên hoặc đã bị khóa. */
    INVALID_TEACHER(HttpStatus.BAD_REQUEST, "Teacher not found or inactive"),
    /** Đổi mã đăng nhập sang mã đã có người dùng (không phân biệt hoa thường). */
    USERNAME_TAKEN(HttpStatus.CONFLICT, "Username already exists"),
    /** Đặt lại mật khẩu về ngày sinh nhưng tài khoản chưa có ngày sinh. */
    BIRTH_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "Birth date is required"),
    ASSESSMENT_CLOSED(HttpStatus.CONFLICT, "A finished exam cannot be changed or deleted"),
    /** Thứ tự gửi lên không khớp danh sách hiện tại (tab khác vừa thêm/xóa): tải lại rồi làm lại. */
    STALE_ORDER(HttpStatus.CONFLICT, "Order does not match current items"),
    ASSESSMENT_NOT_OPEN(HttpStatus.CONFLICT, "Assessment is not open yet"),
    SUBMISSION_LOCKED(HttpStatus.CONFLICT, "Submission already submitted or time is over"),
    SUBMISSION_NOT_FINAL(HttpStatus.CONFLICT, "Submission is still in progress"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
