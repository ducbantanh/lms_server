package lms.leave;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** DTO đơn xin nghỉ, khớp lms_client/lib/leave.ts; mọi id trả ra ngoài là public_id. */
public final class LeaveViews {

    private LeaveViews() {
    }

    public record FileView(String id, String name, String contentType, long size) {
    }

    public record StudentRef(String id, String username, String fullName) {
    }

    /** Buổi học của đơn: ngày, tiết, phòng, giờ bắt đầu. */
    public record MeetingRef(String id, LocalDate date, int startPeriod, int periods, String room, Instant startsAt,
            boolean cancelled) {
    }

    /** status: 0 chờ duyệt, 1 đồng ý, 2 từ chối. deadline: hạn gửi (giờ bắt đầu buổi trừ số giờ quy định). */
    public record LeaveView(String id, MeetingRef meeting, StudentRef student, String reason, int status,
            String teacherNote, String reviewerName, Instant reviewedAt, Instant createdAt, List<FileView> files) {
    }

    /** Trang giảng viên: số giờ phải báo trước và các đơn của khóa học (chờ duyệt trước). */
    public record CourseLeaves(int noticeHours, int maxNoticeHours, List<LeaveView> requests) {
    }

    /** Đơn của chính mình ở một buổi, gắn vào MeetingView của lịch sinh viên. */
    public record MyLeave(String id, int status, String teacherNote) {
    }

    public record ReviewRequest(boolean approve, @Size(max = 500) String note) {
    }

    public record SettingsRequest(@Min(0) @Max(LeaveService.MAX_NOTICE_HOURS) int noticeHours) {
    }
}
