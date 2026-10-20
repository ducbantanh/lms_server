package lms.submission;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import lms.course.CourseViews.AssessmentView;

/** DTO của module bài nộp và chấm điểm, khớp lms_client/lib/teaching.ts; mọi id trả ra ngoài là public_id. */
public final class SubmissionViews {

    private SubmissionViews() {
    }

    /** Trạng thái bài làm của sinh viên, tính theo thời gian của bài và việc đã bấm nộp hay chưa. */
    public static final String NOT_STARTED = "not_started";
    public static final String IN_PROGRESS = "in_progress";
    public static final String SUBMITTED = "submitted";
    /** Hết giờ mà chưa bấm nộp: các file đã tải lên được tính là bài nộp. */
    public static final String AUTO_SUBMITTED = "auto_submitted";
    /** Hết giờ, không có file nào. */
    public static final String MISSED = "missed";

    public record CourseRef(String id, String code, String title) {
    }

    public record SubmissionFileView(String id, String name, String contentType, long size, Instant createdAt) {
    }

    /** Điểm thang 10 và nhận xét; gradedBy là họ tên người chấm (null nếu tài khoản đã bị xóa). */
    public record GradeView(BigDecimal score, String feedback, Instant gradedAt, String gradedBy) {
    }

    /** editable = còn thêm/xóa file và bấm nộp được; grade null = chưa chấm. */
    public record SubmissionView(String status, Instant submittedAt, boolean editable,
            List<SubmissionFileView> files, GradeView grade) {
    }

    /** Kết quả của sinh viên ở một bài, cho trang khóa học (score null = chưa chấm). */
    public record MyResult(String assessmentId, String status, Instant submittedAt, BigDecimal score) {
    }

    // ---------------------------------------------------------------- giảng viên

    /** Một dòng trong danh sách bài nộp: mọi sinh viên đã ghi danh, kể cả chưa nộp. */
    public record SubmissionRow(String studentId, String username, String fullName, String status,
            Instant submittedAt, Instant updatedAt, List<SubmissionFileView> files, GradeView grade) {
    }

    /** submitted gồm cả nộp khi hết giờ; average chỉ tính bài đã chấm (null nếu chưa chấm bài nào). */
    public record SubmissionSummary(int students, int submitted, int graded, BigDecimal average) {
    }

    public record AssessmentSubmissions(CourseRef course, AssessmentView assessment, SubmissionSummary summary,
            List<SubmissionRow> rows) {
    }

    /** score null = xóa điểm và nhận xét. */
    public record GradeRequest(
            @DecimalMin("0") @DecimalMax("10") @Digits(integer = 2, fraction = 2) BigDecimal score,
            @Size(max = 2000) String feedback) {
    }

    /** serverTime để client đếm ngược theo giờ server, không phụ thuộc đồng hồ máy sinh viên. */
    public record StudentAssessment(CourseRef course, AssessmentView assessment, SubmissionView submission,
            Instant serverTime) {
    }
}
