package lms.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    /** Khóa học user đã ghi danh, kèm giảng viên (fetch join để không bị lazy ngoài transaction). */
    @Query("""
            select c from Course c
            left join fetch c.teacher
            where c.id in (select e.courseId from Enrollment e where e.userId = :userId)
            order by c.code
            """)
    List<Course> findEnrolledBy(@Param("userId") long userId);

    @Query("select c from Course c left join fetch c.teacher where c.publicId = :publicId")
    Optional<Course> findByPublicId(@Param("publicId") String publicId);

    Optional<Course> findByEnrollKey(String enrollKey);

    boolean existsByEnrollKey(String enrollKey);

    /** Admin: mọi khóa học, mới nhất trước. */
    @Query("select c from Course c left join fetch c.teacher order by c.createdAt desc")
    List<Course> findAllNewestFirst();

    @Query("select c from Course c left join fetch c.teacher where c.teacher.id = :teacherId order by c.createdAt desc")
    List<Course> findByTeacherNewestFirst(@Param("teacherId") long teacherId);
}
