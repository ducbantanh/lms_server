package lms.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    List<CourseSection> findByCourseIdOrderByPosition(long courseId);

    Optional<CourseSection> findByPublicIdAndCourseId(String publicId, long courseId);

    @Query("select coalesce(max(s.position), 0) from CourseSection s where s.courseId = :courseId")
    int maxPosition(@Param("courseId") long courseId);
}
