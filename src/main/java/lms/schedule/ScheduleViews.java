package lms.schedule;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lms.leave.LeaveViews;

/** DTO lịch học, khớp lms_client/lib/schedule.ts; mọi id trả ra ngoài là public_id, ngày dạng yyyy-MM-dd. */
public final class ScheduleViews {

    private ScheduleViews() {
    }

    public record CourseRef(String id, String code, String title, String teacherName) {
    }

    public record RoomRef(String id, String name, String building) {
    }

    // ================================================================ phòng học

    public record RoomView(String id, String name, String building, Integer capacity, String note, boolean active) {
    }

    /** active null = giữ nguyên (khi tạo: đang dùng). */
    public record RoomRequest(
            @NotBlank @Size(max = 32) String name,
            @Size(max = 64) String building,
            @Min(1) @Max(2000) Integer capacity,
            @Size(max = 200) String note,
            Boolean active) {
    }

    /** Nhập nhiều phòng: mỗi dòng "tên | tòa nhà | sức chứa | ghi chú" (RoomImportParser). */
    public record RoomImportRequest(@NotBlank @Size(max = 262_144) String content) {
    }

    /** Kết quả nhập phòng: đã tạo, trùng (ALREADY_EXISTS / DUPLICATE_IN_INPUT), lỗi theo dòng. */
    public record RoomImportReport(int totalLines, List<RoomView> created,
            List<RoomImportParser.LineIssue> duplicates, List<RoomImportParser.LineIssue> errors) {
    }

    // ================================================================ quy tắc lặp

    /** meetings = số buổi chưa hủy đang gắn với quy tắc (cả buổi đã qua). */
    public record ScheduleView(String id, CourseRef course, RoomRef room, int weekday, int startPeriod, int periods,
            int kind, LocalDate startsOn, LocalDate endsOn, long meetings) {
    }

    /**
     * Tạo cần courseId; khi sửa courseId bị bỏ qua. weekday 1 thứ 2 … 7 chủ nhật; kind 0 lý thuyết / 1 thực hành.
     * notifyStudents: gửi thông báo lịch cho sinh viên đang ghi danh và giảng viên phụ trách.
     */
    public record ScheduleRequest(
            String courseId,
            @NotBlank String roomId,
            @Min(1) @Max(7) int weekday,
            @Min(1) int startPeriod,
            @Min(1) int periods,
            @Min(0) @Max(1) int kind,
            @NotNull LocalDate startsOn,
            @NotNull LocalDate endsOn,
            boolean notifyStudents) {
    }

    /** created = số buổi vừa sinh; removed = số buổi cũ (từ hôm nay, chưa điểm danh) bị thay thế khi sửa. */
    public record ScheduleResult(ScheduleView schedule, int created, int removed) {
    }

    /**
     * Nhập thời khóa biểu: mỗi dòng một lịch lặp (ScheduleImportParser). dryRun: chỉ kiểm tra, không lưu.
     * notifyStudents: gửi mỗi khóa học một thông báo liệt kê lịch vừa nhập.
     */
    public record ScheduleImportRequest(@NotBlank @Size(max = 262_144) String content, boolean dryRun,
            boolean notifyStudents) {
    }

    /** Một dòng hợp lệ của file nhập: lịch sẽ tạo (hoặc đã tạo) và số buổi sinh ra. */
    public record PlannedRule(int line, CourseRef course, RoomRef room, int weekday, int startPeriod, int periods,
            int kind, LocalDate startsOn, LocalDate endsOn, int meetings) {
    }

    /**
     * Một dòng lỗi. reason: lỗi định dạng của ScheduleImportParser, hoặc COURSE_NOT_FOUND / COURSE_AMBIGUOUS /
     * ROOM_NOT_FOUND / ROOM_INACTIVE / INVALID_PERIODS / NO_MEETINGS / CONFLICT (trùng lịch đã có, xem conflicts) /
     * CONFLICT_IN_FILE (trùng dòng otherLine trong cùng file).
     */
    public record ImportIssue(int line, String label, String reason, Integer otherLine, List<Conflict> conflicts) {
    }

    /** imported: đã lưu (chỉ khi không dryRun và không dòng nào lỗi — có lỗi thì không lưu dòng nào). */
    public record ScheduleImportReport(int totalLines, boolean imported, List<PlannedRule> rules, int meetings,
            List<ImportIssue> errors) {
    }

        // ================================================================ buổi học

    /**
     * Một buổi học. originalDate/originalStartPeriod: buổi đã dời, lịch ban đầu. teaching: người xem là giảng viên
     * phụ trách (mở điểm danh được). attendanceSessionId chỉ trả cho người quản lý khóa học.
     * leaveDeadline: hạn gửi đơn xin nghỉ; leave: đơn của chính người xem (sinh viên) ở buổi này, null nếu chưa có;
     * pendingLeaves: số đơn chờ duyệt (chỉ người quản lý khóa học, còn lại 0).
     */
    public record MeetingView(String id, CourseRef course, RoomRef room, String scheduleId, LocalDate date,
            int startPeriod, int periods, Instant startsAt, Instant endsAt, int kind, boolean cancelled,
            LocalDate originalDate, Integer originalStartPeriod, String note, boolean teaching,
            String attendanceSessionId, Instant leaveDeadline, LeaveViews.MyLeave leave, int pendingLeaves) {
    }

    /** Thêm buổi lẻ (buổi bù) cần courseId; khi dời buổi courseId bị bỏ qua. kind null = lý thuyết. */
    public record MeetingRequest(
            String courseId,
            @NotBlank String roomId,
            @NotNull LocalDate date,
            @Min(1) int startPeriod,
            @Min(1) int periods,
            @Min(0) @Max(1) Integer kind,
            @Size(max = 200) String note,
            boolean notifyStudents) {
    }

    public record CancelRequest(@Size(max = 200) String note, boolean notifyStudents) {
    }

    /** reason: room (trùng phòng) / teacher (giảng viên đang dạy lớp khác) / course (khóa học đã có buổi). */
    public record Conflict(LocalDate date, int startPeriod, int periods, String reason, CourseRef course,
            RoomRef room) {
    }

    // ================================================================ lịch của người xem

    /** Mốc mở / đóng bài trong khoảng ngày: kind "opens" / "closes". */
    public record DeadlineView(String assessmentId, CourseRef course, int type, String title, String kind,
            Instant at) {
    }

    public record ScheduleFeed(ScheduleRules periods, LocalDate from, LocalDate to, List<MeetingView> meetings,
            List<DeadlineView> deadlines) {
    }

    public record AttendanceLink(String sessionId) {
    }
}
