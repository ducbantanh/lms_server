package lms.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Envelope chung cho mọi response của /api/v1.
 *
 * <pre>
 * { "success": true,  "data": {...} }
 * { "success": false, "error": { "code": "FORBIDDEN", "message": "...", "details": ... } }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, T data, ApiError error) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ApiError(String code, String message, Object details) {
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<Void> error(ErrorCode code, String message, Object details) {
        return new ApiResponse<>(false, null, new ApiError(code.name(), message, details));
    }
}
