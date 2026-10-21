package lms.report;

import java.time.Instant;
import java.util.List;

/** DTO trang tổng quan giảng viên, khớp lms_client/lib/teaching.ts; mọi id là public_id. */
public final class OverviewViews {

    private OverviewViews() {
    }

    /** students đếm mỗi sinh viên một lần dù học nhiều khóa; toGrade là tổng số bài nộp chờ chấm. */
    public record Stats(int courses, int students, int toGrade, int openAssessments) {
    }

    /** Bài có bài nộp chưa chấm. pending = đã nộp mà chưa chấm; submitted/students như trang bài nộp. */
    public record GradingTask(String courseId, String courseCode, String assessmentId, int type, String title,
            Instant closesAt, int pending, int submitted, int students) {
    }

    /** kind "open": đang mở (closesAt null = không hạn); "upcoming": sẽ mở trong UPCOMING_DAYS ngày tới. */
    public record ScheduleItem(String courseId, String courseCode, String assessmentId, int type, String title,
            String kind, Instant opensAt, Instant closesAt, int submitted, int students) {
    }

    /** averageProgress null khi khóa chưa có sinh viên hoặc chưa có bài học/bài kiểm tra. */
    public record CourseRow(String id, String code, String title, int format, String teacherName, int studentCount,
            Integer averageProgress, int toGrade, int openAssessments, Instant createdAt) {
    }

    /** toGradeMore / scheduleMore: số mục bị cắt khỏi danh sách (chỉ trả tối đa LIST_LIMIT mục). */
    public record TeacherOverview(Stats stats, List<GradingTask> toGrade, int toGradeMore, List<ScheduleItem> schedule,
            int scheduleMore, List<CourseRow> courses, Instant serverTime) {
    }
}
