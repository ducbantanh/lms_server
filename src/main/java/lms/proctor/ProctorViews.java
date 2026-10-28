package lms.proctor;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO giám sát thi, khớp lms_client/lib/proctor.ts; mọi id trả ra ngoài là public_id. */
public final class ProctorViews {

    private ProctorViews() {
    }

    /** Trạng thái phòng thi của sinh viên (StudentProctor.status). */
    public static final String NONE = "none";
    public static final String ACTIVE = "active";
    public static final String LOCKED = "locked";
    public static final String FINISHED = "finished";
    /** Phiên thi đang gắn với máy (phiên đăng nhập) khác. */
    public static final String OTHER_DEVICE = "other_device";
    /** Bảng giám thị: sinh viên chưa vào phòng thi. */
    public static final String NOT_STARTED = "not_started";

    // ---------------------------------------------------------------- sinh viên

    /**
     * Gắn vào StudentAssessment khi bài là bài kiểm tra có giám sát. networkAllowed: IP hiện tại nằm trong mạng
     * được phép; canFinish: bài lập trình kết thúc bằng nút "Kết thúc bài thi" (trắc nghiệm, nộp file kết thúc khi
     * nộp bài).
     */
    public record StudentProctor(String status, int violations, Integer maxViolations, boolean accessCode,
            boolean fullscreen, boolean networkRestricted, boolean networkAllowed, String lockReason,
            Instant startedAt, boolean canFinish) {
    }

    public record StartRequest(@Size(max = 16) String accessCode) {
    }

    /** at: giờ xảy ra theo giờ server (client tự trừ độ lệch đồng hồ). */
    public record ClientEvent(@NotBlank @Size(max = 32) String type, Instant at, @Min(0) Long durationMs,
            @Size(max = 100) String detail) {
    }

    /** Nhịp 15 giây một lần, hoặc ngay khi có sự kiện; away / fullscreen null = không đổi. */
    public record ReportRequest(Boolean away, Boolean fullscreen, @Size(max = 50) List<@Valid ClientEvent> events) {
    }

    /** details của lỗi PROCTOR_IN_EXAM: bài sinh viên đang thi, để client đưa về trang làm bài. */
    public record InExam(String assessmentId, String courseId, String title) {
    }

    /** Sự kiện WebSocket proctor.state gửi sinh viên: giám thị khóa / mở khóa / cho đổi máy; client tải lại bài. */
    public record StateChanged(String assessmentId) {
    }

    // ---------------------------------------------------------------- giảng viên

    /** maxViolations null = chỉ ghi lại vi phạm, không tự khóa bài. */
    public record Settings(boolean enabled, String networks, boolean accessCode, boolean fullscreen,
            Integer maxViolations) {
    }

    public record SettingsRequest(boolean enabled, @Size(max = 500) String networks, boolean accessCode,
            boolean fullscreen, @Min(1) @Max(ProctorRules.MAX_VIOLATION_LIMIT) Integer maxViolations) {
    }

    /** Mã phòng thi đang hiệu lực và lúc đổi mã. */
    public record AccessCode(String code, Instant expiresAt, int periodSeconds) {
    }

    public record CourseRef(String id, String code, String title) {
    }

    public record AssessmentRef(String id, String title, int answerMode, Instant opensAt, Instant closesAt,
            String status) {
    }

    /** actorName: giám thị khóa / mở khóa / cho đổi máy. */
    public record EventView(String type, boolean counted, Long durationMs, String detail, String ip,
            String actorName, Instant at) {
    }

    /**
     * Một sinh viên trên bảng giám thị. status: not_started / active / locked / finished; work: trạng thái bài làm
     * như danh sách bài nộp (SubmissionViews); online: còn nhịp trong 45 giây; ipAllowed: IP gần nhất thuộc mạng
     * được phép.
     */
    public record BoardRow(String studentId, String username, String fullName, String status, String work,
            boolean online, Instant awaySince, boolean fullscreen, int violations, Instant startedAt,
            Instant lastSeenAt, Instant finishedAt, String ip, boolean ipAllowed, String lockReason,
            EventView lastEvent) {
    }

    /** offline: đang thi (active / locked) nhưng mất nhịp; flagged: có ít nhất một vi phạm. */
    public record BoardSummary(int students, int notStarted, int active, int offline, int locked, int finished,
            int flagged) {
    }

    /** canEdit: giảng viên phụ trách (sửa cài đặt); admin chỉ xem, vẫn khóa / mở khóa được. */
    public record Board(CourseRef course, AssessmentRef assessment, Settings settings, AccessCode accessCode,
            BoardSummary summary, List<BoardRow> rows, boolean canEdit, Instant serverTime) {
    }

    public record StudentLog(BoardRow row, String userAgent, String startIp, List<EventView> events) {
    }

    public record LockRequest(@Size(max = 200) String reason) {
    }

    /** Sự kiện WebSocket proctor.updated gửi giảng viên phụ trách khi một dòng của bảng giám thị đổi. */
    public record RowChanged(String courseId, String assessmentId, BoardRow row) {
    }
}
