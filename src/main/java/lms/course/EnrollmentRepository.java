package lms.course;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Enrollment.Key> {

    boolean existsByCourseIdAndUserId(long courseId, long userId);

    long countByCourseId(long courseId);

    /** Số sinh viên theo từng khóa học: [courseId, count]. */
    @Query("select e.courseId, count(e) from Enrollment e where e.courseId in :ids group by e.courseId")
    List<Object[]> countByCourseIds(@Param("ids") Collection<Long> ids);

    /** Danh sách ghi danh kèm user: [Enrollment, User], sắp theo username. */
    @Query("""
            select e, u from Enrollment e join User u on u.id = e.userId
            where e.courseId = :courseId
            order by u.username
            """)
    List<Object[]> findWithUsers(@Param("courseId") long courseId);

    @Modifying
    @Query("delete from Enrollment e where e.courseId = :courseId and e.userId = :userId")
    int deleteOne(@Param("courseId") long courseId, @Param("userId") long userId);
}
