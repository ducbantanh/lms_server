package lms.common;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpStatus;

/**
 * Mã lỗi nghiệp vụ chuẩn hóa toàn hệ thống.
 * Frontend dựa vào "code" để hiển thị giao diện phù hợp.
 * Hỗ trợ phân loại nhóm lỗi (Category) và thông điệp song ngữ (Anh - Việt).
 */
public enum ErrorCode {

    // === NHÓM 1: XÁC THỰC VÀ BẢO MẬT (AUTH & SECURITY) ===
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Login required", "Yêu cầu đăng nhập", ErrorCategory.AUTH),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid username or password", "Tên đăng nhập hoặc mật khẩu không chính xác", ErrorCategory.AUTH),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Account is disabled", "Tài khoản đã bị tạm khóa hoặc ngừng hoạt động", ErrorCategory.AUTH),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "Too many attempts, try again later", "Quá nhiều lần thử sai, vui lòng thử lại sau", ErrorCategory.AUTH),
    PASSWORD_CHANGE_REQUIRED(HttpStatus.FORBIDDEN, "Password change required", "Bắt buộc đổi mật khẩu trước khi tiếp tục", ErrorCategory.AUTH),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Permission denied", "Bạn không có quyền thực hiện thao tác này", ErrorCategory.SECURITY),
    CSRF_INVALID(HttpStatus.FORBIDDEN, "Invalid CSRF token", "Token CSRF không hợp lệ hoặc đã hết hạn", ErrorCategory.SECURITY),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded", "Tần suất gửi yêu cầu vượt quá giới hạn cho phép", ErrorCategory.SECURITY),
    IP_BLOCKED(HttpStatus.FORBIDDEN, "Access from this IP is blocked", "Địa chỉ IP này đã bị chặn truy cập", ErrorCategory.SECURITY),
    MALICIOUS_INPUT_DETECTED(HttpStatus.BAD_REQUEST, "Malicious input detected", "Phát hiện nội dung không an toàn trong dữ liệu gửi lên", ErrorCategory.SECURITY),

    // === NHÓM 2: YÊU CẦU CHUNG VÀ XỬ LÝ DỮ LIỆU (GENERAL HTTP & VALIDATION) ===
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Invalid request", "Dữ liệu yêu cầu không hợp lệ", ErrorCategory.VALIDATION),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request", "Yêu cầu không hợp lệ", ErrorCategory.REQUEST),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found", "Không tìm thấy tài nguyên yêu cầu", ErrorCategory.REQUEST),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", "Phương thức HTTP không được hỗ trợ", ErrorCategory.REQUEST),
    CONFLICT(HttpStatus.CONFLICT, "Conflict", "Xảy ra xung đột dữ liệu", ErrorCategory.REQUEST),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "Payload too large", "Kích thước tập tin hoặc dữ liệu vượt quá giới hạn", ErrorCategory.REQUEST),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "Unsupported file type", "Định dạng tập tin không được hỗ trợ", ErrorCategory.FILE),
    FILE_CORRUPTED(HttpStatus.BAD_REQUEST, "Uploaded file is corrupted or unreadable", "Tập tin tải lên bị hỏng hoặc không thể đọc", ErrorCategory.FILE),
    CHECKSUM_MISMATCH(HttpStatus.BAD_REQUEST, "File integrity check failed", "Kiểm tra tính toàn vẹn tập tin thất bại", ErrorCategory.FILE),
    EXPORT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate export file", "Xuất dữ liệu thất bại", ErrorCategory.SYSTEM),

    // === NHÓM 3: KHÓA HỌC VÀ THÀNH VIÊN (COURSE & ENROLLMENT) ===
    INVALID_ENROLL_KEY(HttpStatus.BAD_REQUEST, "Invalid enroll key", "Mã tham gia khóa học không chính xác", ErrorCategory.COURSE),
    TEACHER_ONLY(HttpStatus.FORBIDDEN, "Only the course teacher can do this", "Chỉ giảng viên phụ trách khóa học mới có quyền thực hiện", ErrorCategory.COURSE),
    INVALID_TEACHER(HttpStatus.BAD_REQUEST, "Teacher not found or inactive", "Giảng viên không tồn tại hoặc đã ngừng hoạt động", ErrorCategory.COURSE),
    COURSE_GROUP_TAKEN(HttpStatus.CONFLICT, "A course with this code and group already exists", "Mã học phần và nhóm lớp này đã tồn tại", ErrorCategory.COURSE),
    USERNAME_TAKEN(HttpStatus.CONFLICT, "Username already exists", "Tên đăng nhập này đã được sử dụng", ErrorCategory.USER),
    BIRTH_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "Birth date is required", "Tài khoản cần có thông tin ngày sinh để thực hiện", ErrorCategory.USER),
    ASSESSMENT_CLOSED(HttpStatus.CONFLICT, "A finished exam cannot be changed or deleted", "Bài kiểm tra đã kết thúc không thể chỉnh sửa hoặc xóa", ErrorCategory.ASSESSMENT),
    STALE_ORDER(HttpStatus.CONFLICT, "Order does not match current items", "Thứ tự các mục không đồng bộ, vui lòng tải lại trang", ErrorCategory.COURSE),
    ASSESSMENT_NOT_OPEN(HttpStatus.CONFLICT, "Assessment is not open yet", "Bài kiểm tra chưa đến thời gian mở", ErrorCategory.ASSESSMENT),
    SUBMISSION_LOCKED(HttpStatus.CONFLICT, "Submission already submitted or time is over", "Bài nộp đã khóa hoặc đã quá hạn làm bài", ErrorCategory.SUBMISSION),
    SUBMISSION_NOT_FINAL(HttpStatus.CONFLICT, "Submission is still in progress", "Bài nộp vẫn đang trong quá trình thực hiện", ErrorCategory.SUBMISSION),

    // === NHÓM 4: ĐIỂM DANH (ATTENDANCE) ===
    ATTENDANCE_CODE_INVALID(HttpStatus.BAD_REQUEST, "Attendance code is invalid or expired", "Mã điểm danh không hợp lệ hoặc đã hết hạn", ErrorCategory.ATTENDANCE),
    ATTENDANCE_CLOSED(HttpStatus.CONFLICT, "QR check-in is closed for this session", "Phiên điểm danh bằng mã QR đã đóng", ErrorCategory.ATTENDANCE),
    NOT_ENROLLED(HttpStatus.FORBIDDEN, "You are not enrolled in this course", "Bạn không có tên trong danh sách sinh viên của học phần này", ErrorCategory.COURSE),

    // === NHÓM 5: NGÂN HÀNG CÂU HỎI VÀ MÁY CHẤM CODE (EXAM & JUDGE) ===
    QUESTION_LIMIT(HttpStatus.CONFLICT, "Too many questions", "Vượt quá số lượng câu hỏi cho phép", ErrorCategory.QUESTION),
    JUDGE_BUSY(HttpStatus.CONFLICT, "Previous attempt is still being judged", "Lần nộp trước đang được chấm, vui lòng đợi trong giây lát", ErrorCategory.JUDGE),
    ATTEMPT_LIMIT(HttpStatus.CONFLICT, "No submissions left", "Bạn đã hết số lần nộp bài cho phép", ErrorCategory.JUDGE),

    // === NHÓM 6: THỜI KHÓA BIỂU VÀ PHÒNG HỌC (SCHEDULE & ROOM) ===
    SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "Schedule conflicts with existing classes", "Trùng lịch học với buổi khác cùng phòng hoặc giảng viên", ErrorCategory.SCHEDULE),
    ROOM_NAME_TAKEN(HttpStatus.CONFLICT, "A room with this name already exists", "Tên phòng học này đã tồn tại trong hệ thống", ErrorCategory.SCHEDULE),
    ROOM_IN_USE(HttpStatus.CONFLICT, "Room is used by schedules", "Phòng học đang có lịch xếp lớp, không thể xóa", ErrorCategory.SCHEDULE),
    ROOM_INACTIVE(HttpStatus.CONFLICT, "Room is no longer in use", "Phòng học này đã ngừng hoạt động", ErrorCategory.SCHEDULE),
    MEETING_HAS_ATTENDANCE(HttpStatus.CONFLICT, "Attendance was already taken for this class", "Buổi học đã có dữ liệu điểm danh, không thể dời hoặc xóa", ErrorCategory.SCHEDULE),
    MEETING_CANCELLED(HttpStatus.CONFLICT, "This class was cancelled", "Buổi học này đã bị hủy", ErrorCategory.SCHEDULE),
    MEETING_NOT_STARTED(HttpStatus.CONFLICT, "This class has not taken place yet", "Buổi học chưa diễn ra", ErrorCategory.SCHEDULE),

    // === NHÓM 7: NGHỈ PHÉP (LEAVE REQUEST) ===
    LEAVE_DEADLINE_PASSED(HttpStatus.CONFLICT, "Too late to request leave for this class", "Đã quá hạn gửi đơn xin nghỉ cho buổi học này", ErrorCategory.LEAVE),
    LEAVE_ALREADY_REQUESTED(HttpStatus.CONFLICT, "Leave already requested for this class", "Bạn đã gửi đơn xin nghỉ cho buổi học này rồi", ErrorCategory.LEAVE),
    LEAVE_ALREADY_REVIEWED(HttpStatus.CONFLICT, "Leave request was already reviewed", "Đơn xin nghỉ đã được giảng viên xử lý, không thể thu hồi", ErrorCategory.LEAVE),

    // === NHÓM 8: GIÁM SÁT THI TRỰC TUYẾN (PROCTORING) ===
    PROCTOR_REQUIRED(HttpStatus.FORBIDDEN, "Enter the exam room first", "Vui lòng vào phòng thi trực tuyến trước khi xem đề", ErrorCategory.PROCTOR),
    PROCTOR_LOCKED(HttpStatus.FORBIDDEN, "Exam is locked by the proctor", "Bài thi đã bị khóa bởi giám thị do phát hiện vi phạm", ErrorCategory.PROCTOR),
    PROCTOR_OTHER_DEVICE(HttpStatus.FORBIDDEN, "Exam is in progress on another device", "Bài thi đang được thực hiện trên một thiết bị khác", ErrorCategory.PROCTOR),
    PROCTOR_NETWORK_DENIED(HttpStatus.FORBIDDEN, "This network is not allowed for the exam", "Địa chỉ mạng hiện tại không thuộc dải IP cho phép của phòng thi", ErrorCategory.PROCTOR),
    PROCTOR_CODE_INVALID(HttpStatus.BAD_REQUEST, "Exam room code is invalid or expired", "Mã vào phòng thi không chính xác hoặc đã hết hạn", ErrorCategory.PROCTOR),
    PROCTOR_FINISHED(HttpStatus.CONFLICT, "Exam already finished", "Bài thi đã hoàn thành và nộp bài, không thể tiếp tục", ErrorCategory.PROCTOR),
    PROCTOR_IN_EXAM(HttpStatus.FORBIDDEN, "Only the exam page is available during a proctored exam", "Trong khi đang làm bài thi có giám sát, bạn không thể truy cập trang khác", ErrorCategory.PROCTOR),

    // === NHÓM 9: LỖI HỆ THỐNG NỘI BỘ (INTERNAL SERVER ERRORS) ===
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable", "Dịch vụ tạm thời gián đoạn, vui lòng thử lại sau", ErrorCategory.SYSTEM),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "Đã xảy ra lỗi nội bộ máy chủ", ErrorCategory.SYSTEM);

    public enum ErrorCategory {
        AUTH,
        SECURITY,
        VALIDATION,
        REQUEST,
        FILE,
        COURSE,
        USER,
        ASSESSMENT,
        SUBMISSION,
        ATTENDANCE,
        QUESTION,
        JUDGE,
        SCHEDULE,
        LEAVE,
        PROCTOR,
        SYSTEM
    }

    private final HttpStatus status;
    private final String defaultMessage;
    private final String vietnameseMessage;
    private final ErrorCategory category;

    ErrorCode(HttpStatus status, String defaultMessage, String vietnameseMessage, ErrorCategory category) {
        this.status = status;
        this.defaultMessage = defaultMessage;
        this.vietnameseMessage = vietnameseMessage;
        this.category = category;
    }

    ErrorCode(HttpStatus status, String defaultMessage) {
        this(status, defaultMessage, defaultMessage, ErrorCategory.REQUEST);
    }

    public HttpStatus status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }

    public String vietnameseMessage() {
        return vietnameseMessage;
    }

    public ErrorCategory category() {
        return category;
    }

    public boolean isClientError() {
        return status.is4xxClientError();
    }

    public boolean isServerError() {
        return status.is5xxServerError();
    }

    public static Optional<ErrorCode> fromName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name.trim()))
                .findFirst();
    }
}
