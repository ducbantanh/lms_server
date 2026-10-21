package lms.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseFileRepository extends JpaRepository<CourseFile, Long> {

    List<CourseFile> findByCourseIdOrderByCreatedAt(long courseId);

    Optional<CourseFile> findByPublicId(String publicId);

    Optional<CourseFile> findByPublicIdAndCourseId(String publicId, long courseId);

    /** Xóa mục: tài liệu trong mục chuyển sang "chưa phân mục" (FK cũng SET NULL, cập nhật luôn để cache JPA khớp). */
    @Modifying
    @Query("update CourseFile f set f.sectionId = null where f.sectionId = :sectionId")
    int detachFromSection(@Param("sectionId") long sectionId);

    /** Xóa bài: tài liệu đính kèm giữ lại trong mục của bài. */
    @Modifying
    @Query("update CourseFile f set f.lessonId = null where f.lessonId = :lessonId")
    int detachFromLesson(@Param("lessonId") long lessonId);

    /** Bài chuyển mục: tài liệu đính kèm đi theo để sectionId luôn khớp với bài. */
    @Modifying
    @Query("update CourseFile f set f.sectionId = :sectionId where f.lessonId = :lessonId")
    int moveWithLesson(@Param("lessonId") long lessonId, @Param("sectionId") Long sectionId);
}
