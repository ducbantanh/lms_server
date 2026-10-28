package lms.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Xử lý ngoại lệ tập trung toàn ứng dụng (Centralized Global Exception Handler).
 * Chuyển đổi mọi ngoại lệ thành định dạng chuẩn {@link ApiResponse}.
 * Tuyệt đối không để lộ stack trace hoặc thông tin bảo mật nhạy cảm của máy chủ ra client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Xử lý ngoại lệ nghiệp vụ do ứng dụng chủ động ném ra (AppException).
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleApp(AppException ex) {
        log.debug("AppException [{}]: {}", ex.code(), ex.getMessage());
        return build(ex.code(), ex.getMessage(), ex.details());
    }

    /**
     * Xử lý lỗi validate form dữ liệu DTO (@Valid, @Validated).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fields = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("field", fe.getField());
            item.put("message", fe.getDefaultMessage() == null ? "Không hợp lệ" : fe.getDefaultMessage());
            item.put("rejectedValue", fe.getRejectedValue() == null ? "null" : String.valueOf(fe.getRejectedValue()));
            fields.add(item);
        }
        return build(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.defaultMessage(), fields);
    }

    /**
     * Xử lý lỗi vi phạm ràng buộc trên bean/tham số controller (@Validated trên class).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> violations = new ArrayList<>();
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("property", cv.getPropertyPath().toString());
            item.put("message", cv.getMessage());
            violations.add(item);
        }
        return build(ErrorCode.VALIDATION_FAILED, "Dữ liệu ràng buộc không thỏa mãn", violations);
    }

    /**
     * Xử lý lỗi định dạng dữ liệu HTTP request (JSON sai cú pháp, thiếu tham số, sai kiểu).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed HTTP message: {}", ex.getMessage());
        Map<String, String> details = Map.of(
                "reason", "Malformed JSON or unreadable request body"
        );
        return build(ErrorCode.BAD_REQUEST, "Cú pháp dữ liệu yêu cầu không hợp lệ", details);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, String> details = Map.of(
                "parameter", ex.getParameterName(),
                "expectedType", ex.getParameterType()
        );
        return build(ErrorCode.BAD_REQUEST, "Thiếu tham số bắt buộc: " + ex.getParameterName(), details);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPart(MissingServletRequestPartException ex) {
        Map<String, String> details = Map.of(
                "part", ex.getRequestPartName()
        );
        return build(ErrorCode.BAD_REQUEST, "Thiếu dữ liệu tệp hoặc multipart: " + ex.getRequestPartName(), details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String reqType = ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName();
        Map<String, String> details = Map.of(
                "parameter", ex.getName(),
                "requiredType", reqType,
                "providedValue", String.valueOf(ex.getValue())
        );
        return build(ErrorCode.BAD_REQUEST, "Tham số '" + ex.getName() + "' không đúng định dạng", details);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return build(ErrorCode.BAD_REQUEST, "Loại dữ liệu (Content-Type) không được hỗ trợ", null);
    }

    /**
     * Xử lý lỗi dung lượng tập tin tải lên vượt quá mức cho phép.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleTooLarge(MaxUploadSizeExceededException ex) {
        log.warn("File upload size limit exceeded: {}", ex.getMessage());
        return build(ErrorCode.PAYLOAD_TOO_LARGE, "Dung lượng tệp tải lên vượt quá giới hạn tối đa", null);
    }

    /**
     * Xử lý lỗi vi phạm ràng buộc cơ sở dữ liệu (Unique constraint, Foreign key conflict).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Database integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return build(ErrorCode.CONFLICT, "Dữ liệu bị trùng lặp hoặc vi phạm ràng buộc toàn vẹn", null);
    }

    /**
     * Xử lý lỗi phương thức HTTP không được hỗ trợ trên endpoint (GET/POST/PUT/DELETE).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethod(HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> details = Map.of(
                "supportedMethods", ex.getSupportedHttpMethods() != null ? ex.getSupportedHttpMethods() : Collections.emptySet()
        );
        return build(ErrorCode.METHOD_NOT_ALLOWED, "Phương thức HTTP không được hỗ trợ", details);
    }

    /**
     * Xử lý 404 Not Found:
     * - Yêu cầu bắt đầu bằng /api/ trả về JSON cấu trúc chuẩn.
     * - Các tài nguyên trang tĩnh trả về chuỗi text ngắn gọn.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/api/")) {
            return build(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.defaultMessage(), null);
        }
        return ResponseEntity.status(404).contentType(MediaType.TEXT_PLAIN).body("Not found");
    }

    /**
     * Bắt tất cả các lỗi không lường trước (Unhandled unexpected internal exceptions).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled error on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        return build(ErrorCode.INTERNAL_ERROR, ErrorCode.INTERNAL_ERROR.defaultMessage(), null);
    }

    private static ResponseEntity<ApiResponse<Void>> build(ErrorCode code, String message, Object details) {
        return ResponseEntity.status(code.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(code, message, details));
    }
}
