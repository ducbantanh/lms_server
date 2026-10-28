package lms.common;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Envelope chuẩn hóa toàn diện cho tất cả phản hồi API (/api/v1).
 *
 * <pre>
 * Thành công:
 * {
 *   "success": true,
 *   "data": { ... },
 *   "timestamp": "2026-10-28T08:00:00Z"
 * }
 *
 * Thất bại:
 * {
 *   "success": false,
 *   "error": {
 *     "code": "FORBIDDEN",
 *     "message": "Permission denied",
 *     "details": { ... }
 *   },
 *   "timestamp": "2026-10-28T08:00:00Z"
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError error,
        Instant timestamp,
        Map<String, Object> meta) {

    /** Constructor tương thích ngược cho 3 tham số ban đầu */
    public ApiResponse(boolean success, T data, ApiError error) {
        this(success, data, error, Instant.now(), null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ApiError(
            String code,
            String message,
            Object details,
            String vietnameseMessage) {

        /** Constructor tương thích ngược 3 tham số */
        public ApiError(String code, String message, Object details) {
            this(code, message, details, null);
        }
    }

    // === CÁC HÀM TIỆN ÍCH TẠO PHẢN HỒI THÀNH CÔNG (SUCCESS BUILDERS) ===

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, Instant.now(), null);
    }

    public static <T> ApiResponse<T> ok(T data, Map<String, Object> meta) {
        return new ApiResponse<>(true, data, null, Instant.now(), meta);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null, Instant.now(), null);
    }

    // === CÁC HÀM TIỆN ÍCH TẠO PHẢN HỒI LỖI (ERROR BUILDERS) ===

    public static ApiResponse<Void> error(ErrorCode code, String message, Object details) {
        return new ApiResponse<>(false, null, new ApiError(code.name(), message, details, code.vietnameseMessage()), Instant.now(), null);
    }

    public static ApiResponse<Void> error(ErrorCode code, String message) {
        return error(code, message, null);
    }

    public static ApiResponse<Void> error(ErrorCode code) {
        return error(code, code.defaultMessage(), null);
    }

    public static ApiResponse<Void> badRequest(String message) {
        return error(ErrorCode.BAD_REQUEST, message);
    }

    public static ApiResponse<Void> notFound(String message) {
        return error(ErrorCode.NOT_FOUND, message);
    }

    public static ApiResponse<Void> forbidden(String message) {
        return error(ErrorCode.FORBIDDEN, message);
    }

    public static ApiResponse<Void> conflict(String message) {
        return error(ErrorCode.CONFLICT, message);
    }

    public static ApiResponse<Void> validationFailed(Object details) {
        return error(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.defaultMessage(), details);
    }
}
