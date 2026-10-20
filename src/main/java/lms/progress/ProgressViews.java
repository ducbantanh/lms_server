package lms.progress;

import java.util.List;

/** DTO tiến độ học, khớp lms_client/lib/teaching.ts; mọi id trả ra ngoài là public_id. */
public final class ProgressViews {

    private ProgressViews() {
    }

    /** Bài học đầu tiên chưa học theo thứ tự hiển thị; sectionId null = chưa phân mục. */
    public record NextLesson(String id, String sectionId, String title) {
    }

    /**
     * Tiến độ của một sinh viên ở một khóa học. percent làm tròn xuống (100 chỉ khi xong hết),
     * null khi khóa học chưa có bài học hay bài kiểm tra nào; nextLesson null khi đã học hết bài học.
     */
    public record CourseProgress(int lessonsDone, int lessonsTotal, int assessmentsDone, int assessmentsTotal,
            Integer percent, NextLesson nextLesson, List<String> completedLessonIds) {
    }

    public record StudentProgress(String studentId, int lessonsDone, int assessmentsDone, Integer percent) {
    }

    /** Tiến độ mọi sinh viên đã ghi danh, cho tab Sinh viên của giảng viên. */
    public record CourseProgressReport(int lessonsTotal, int assessmentsTotal, List<StudentProgress> students) {
    }
}
