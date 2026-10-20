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
    /** enrollKey, teacherId luôn null ở góc nhìn sinh viên; teacherId null = chưa phân công. */
    public record CourseView(String id, String code, String title, String description, int format,
            String enrollKey, long studentCount, String teacherId, String teacherName, Instant createdAt) {
    }

    public record SectionView(String id, int position, String title) {
    }

    /** lessonId khác null: tài liệu đính kèm của bài đó (sectionId là mục của bài). */
    public record FileView(String id, String sectionId, String lessonId, String name, String contentType,
            long size, boolean hidden, Instant createdAt) {
    }

    /** sectionId null = chưa phân mục; sắp theo position trong từng mục. */
    public record LessonView(String id, String sectionId, int position, String title, String body,
            boolean hidden, Instant createdAt, Instant updatedAt) {
    }

    /** paperFileId/paperFileName null khi không có đề hoặc sinh viên chưa đến giờ mở. */
    public record AssessmentView(String id, int type, String title, String description, String paperFileId,
            String paperFileName, Instant opensAt, Instant closesAt, String status) {
    }

    public record CourseDetail(CourseView course, List<SectionView> sections, List<LessonView> lessons,
            List<FileView> files, List<AssessmentView> assessments) {
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

    /** Admin tạo khóa học và phân công luôn giảng viên phụ trách (public id, phải là giảng viên đang hoạt động). */
    public record CreateCourseRequest(
            @NotBlank @Size(max = 32) String code,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @Min(0) @Max(2) int format,
            @NotBlank String teacherId) {
    }

    /** Đổi giảng viên phụ trách. */
    public record AssignTeacherRequest(@NotBlank String teacherId) {
    }

    /** Sửa thông tin khóa học; format chỉ đổi được giữa 0 và 1. */
    public record UpdateCourseRequest(
            @NotBlank @Size(max = 32) String code,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @Min(0) @Max(2) int format) {
    }

    public record SectionRequest(@NotBlank @Size(max = 200) String title) {
    }

    /** Thứ tự mới: đủ mọi id mục của khóa học, không thừa không thiếu. */
    public record SectionOrderRequest(@NotNull @Size(max = 500) List<String> ids) {
    }

    /** Thứ tự mới của mọi bài trong một mục (sectionId null = chưa phân mục), kể cả bài đang ẩn. */
    public record LessonOrderRequest(String sectionId, @NotNull @Size(max = 500) List<String> ids) {
    }

    /** Dùng cho cả tạo và sửa (sửa là ghi đè toàn bộ); notifyStudents chỉ có tác dụng khi tạo bài không ẩn. */
    public record LessonRequest(
            String sectionId,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 20000) String body,
            boolean hidden,
            boolean notifyStudents) {
    }

    public record PasteRequest(@NotBlank @Size(max = 20000) String content) {
    }

    public record PickRequest(@NotNull @Size(max = 500) List<String> userIds) {
    }

    public record HiddenRequest(boolean hidden) {
    }

    /** notifyStudents chỉ áp dụng cho bài tập; bài kiểm tra và bài thực hành luôn thông báo cho sinh viên. */
    public record AssessmentRequest(
            @Min(0) @Max(2) int type,
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            String paperFileId,
            Instant opensAt,
            Instant closesAt,
            boolean notifyStudents) {
    }

    /** Sửa bài: tiêu đề, mô tả, giờ mở/đóng; loại bài và đề giữ nguyên. */
    public record AssessmentUpdateRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            Instant opensAt,
            Instant closesAt) {
    }

    public record JoinRequest(@NotBlank @Size(max = 32) String key) {
    }
}
