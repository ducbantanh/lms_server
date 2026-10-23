package lms.attendance;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO điểm danh, khớp lms_client/lib/attendance.ts; mọi id trả ra ngoài là public_id. */
public final class AttendanceViews {

    private AttendanceViews() {
    }

    /** Một buổi kèm số sinh viên (đang ghi danh) theo trạng thái; vắng = sĩ số − ba số còn lại. */
    public record SessionSummary(String id, String title, boolean qrOpen, Instant createdAt, int present, int late,
            int excused) {
    }

    public record AttendanceOverview(int students, List<SessionSummary> sessions) {
    }

    /** Mọi sinh viên đang ghi danh; status/method/checkedAt null = vắng. ip chỉ có khi tự quét QR. */
    public record AttendanceRow(String studentId, String username, String fullName, Integer status, Integer method,
            Instant checkedAt, String ip) {
    }

    public record SessionDetail(SessionSummary session, int students, List<AttendanceRow> rows) {
    }

    /**
     * Mã cho màn chiếu: hiện tới expiresAt rồi lấy mã mới. serverTime để client bù lệch đồng hồ;
     * checkedIn = số sinh viên có mặt hoặc đi muộn trên tổng số đang ghi danh.
     * course (mã kèm nhóm) và session (tên buổi) để màn chiếu không phải tải cả khóa học.
     */
    public record QrCode(String course, String session, String token, Instant expiresAt, Instant serverTime,
            int periodSeconds, int checkedIn, int students) {
    }

    /** Sự kiện realtime attendance.checked_in gửi giảng viên phụ trách. */
    public record CheckedIn(String courseId, String sessionId, AttendanceRow row) {
    }

    /** Kết quả quét của sinh viên; alreadyRecorded: đã có trạng thái từ trước (quét lại, hoặc giảng viên đã điểm). */
    public record CheckInResult(String courseId, String course, String courseTitle, String session, int status,
            Instant checkedAt, boolean alreadyRecorded) {
    }

    /** Lịch sử điểm danh của sinh viên ở một khóa học, buổi cũ trước; status null = vắng. */
    public record MyAttendance(String sessionId, String title, Instant createdAt, Integer status, Instant checkedAt) {
    }

    /** title trống: "Buổi N"; qr: mở điểm danh bằng QR ngay (false khi chỉ điểm danh thủ công). */
    public record CreateSessionRequest(@Size(max = 200) String title, boolean qr) {
    }

    public record RenameRequest(@NotBlank @Size(max = 200) String title) {
    }

    /** Điểm danh thủ công một hoặc nhiều sinh viên; status null = vắng (xóa trạng thái). */
    public record MarkRequest(@NotNull @Size(min = 1, max = 500) List<String> studentIds, Integer status) {
    }

    public record CheckInRequest(@NotBlank @Size(max = 128) String token) {
    }
}
