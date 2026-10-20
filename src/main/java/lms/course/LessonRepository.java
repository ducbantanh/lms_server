package lms.course;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseIdOrderByPositionAscIdAsc(long courseId);

    Optional<Lesson> findByPublicIdAndCourseId(String publicId, long courseId);

    /** Bài học sinh viên thấy được của nhiều khóa học (tính tiến độ). */
    @Query("select l from Lesson l where l.courseId in :courseIds and l.hidden = false")
    List<Lesson> findVisibleIn(@Param("courseIds") Collection<Long> courseIds);

    @Query("select count(l) from Lesson l where l.courseId = :courseId and l.hidden = false")
    long countVisible(@Param("courseId") long courseId);

    /** Số bài học đang hiện theo từng khóa học: [courseId, count]. */
    @Query("select l.courseId, count(l) from Lesson l where l.courseId in :courseIds and l.hidden = false"
            + " group by l.courseId")
    List<Object[]> countVisibleByCourse(@Param("courseIds") Collection<Long> courseIds);

    /** Vị trí lớn nhất trong một mục; sectionId null là nhóm "chưa phân mục". */
    @Query("select coalesce(max(l.position), 0) from Lesson l where l.courseId = :courseId"
            + " and ((:sectionId is null and l.sectionId is null) or l.sectionId = :sectionId)")
    int maxPosition(@Param("courseId") long courseId, @Param("sectionId") Long sectionId);

    /** Xóa mục: bài trong mục chuyển sang "chưa phân mục" (FK cũng SET NULL, cập nhật luôn để cache JPA khớp). */
    @Modifying
    @Query("update Lesson l set l.sectionId = null where l.sectionId = :sectionId")
    int detachFromSection(@Param("sectionId") long sectionId);
}
