package lms.progress;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonCompletionRepository extends JpaRepository<LessonCompletion, LessonCompletion.Key> {

    /** Bài học (đang hiện) mà sinh viên đã học trong các khóa học. */
    @Query("select c.lessonId from LessonCompletion c, Lesson l"
            + " where c.lessonId = l.id and c.userId = :userId and l.courseId in :courseIds and l.hidden = false")
    List<Long> lessonIdsDone(@Param("userId") long userId, @Param("courseIds") Collection<Long> courseIds);

    /** Số bài học (đang hiện) đã học theo từng sinh viên của một khóa học: [userId, count]. */
    @Query("select c.userId, count(c) from LessonCompletion c, Lesson l"
            + " where c.lessonId = l.id and l.courseId = :courseId and l.hidden = false group by c.userId")
    List<Object[]> countByUser(@Param("courseId") long courseId);

    /** Như countByUser cho nhiều khóa học: [courseId, userId, count]. */
    @Query("select l.courseId, c.userId, count(c) from LessonCompletion c, Lesson l"
            + " where c.lessonId = l.id and l.courseId in :courseIds and l.hidden = false"
            + " group by l.courseId, c.userId")
    List<Object[]> countByCourseAndUser(@Param("courseIds") Collection<Long> courseIds);

    /** Bấm hai lần (hoặc hai tab cùng lúc) không lỗi trùng khóa. Giờ UTC như mọi cột DATETIME khác. */
    @Modifying
    @Query(nativeQuery = true, value = "insert ignore into lesson_completions (lesson_id, user_id, completed_at)"
            + " values (:lessonId, :userId, utc_timestamp(3))")
    int insertIgnore(@Param("lessonId") long lessonId, @Param("userId") long userId);

    @Modifying
    @Query("delete from LessonCompletion c where c.lessonId = :lessonId and c.userId = :userId")
    int deleteOne(@Param("lessonId") long lessonId, @Param("userId") long userId);
}
