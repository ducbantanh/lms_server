package lms.course;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO của module khóa học, khớp hợp đồng mà client đang dùng
 * (lms_client/lib/teaching.ts; trước đó là mock-server của client HTML cũ).
 * Mọi id trả ra ngoài là public_id.
 */
public final class CourseViews {

    private CourseViews() {
    }

    // ---------------------------------------------------------------- response

    public record CourseSummary(String id, String code, String title, int format, long studentCount,
            String teacherName, Instant createdAt) {
    }

    /** enrollKey luôn null ở góc nhìn sinh viên. */
    public record CourseView(String id, String code, String title, String description, int format,
            String enrollKey, long studentCount, String teacherName, Instant createdAt) {
    }

    public record SectionView(String id, int position, String title) {
    }

    public record FileView(String id, String sectionId, String name, String contentType, long size,
            boolean hidden, Instant createdAt) {
    }

    /** paperFileId/paperFileName null khi không có đề hoặc sinh viên chưa đến giờ mở. */
    public record AssessmentView(String id, int type, String title, String description, String paperFileId,
            String paperFileName, Instant opensAt, Instant closesAt, String status) {
    }

    public record CourseDetail(CourseView course, List<SectionView> sections, List<FileView> files,
            List<AssessmentView> assessments) {
    }

    public record StudentRow(String id, String username, String fullName, int method, Instant enrolledAt) {
    }

    public record UserPick(String id, String username, String fullName) {
    }

    public record EnrollReport(List<UserPick> added, List<String> alreadyEnrolled, List<String> notFound,
            List<String> notStudent) {
    }

    public record EnrollKey(String enrollKey) {
    }

    // ---------------------------------------------------------------- request

    public record CreateCourseRequest(
            @NotBlank @Size(max = 32) String code,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @Min(0) @Max(2) int format) {
    }

    public record SectionRequest(@NotBlank @Size(max = 200) String title) {
    }

    public record PasteRequest(@NotBlank @Size(max = 20000) String content) {
    }

    public record PickRequest(@NotNull @Size(max = 500) List<String> userIds) {
    }

    public record HiddenRequest(boolean hidden) {
    }

    public record AssessmentRequest(
            @Min(0) @Max(2) int type,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            String paperFileId,
            Instant opensAt,
            Instant closesAt) {
    }

    public record JoinRequest(@NotBlank @Size(max = 32) String key) {
    }
}
