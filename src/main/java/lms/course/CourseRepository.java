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

    /** Mọi nhóm của một học phần (so mã theo collation của bảng: không phân biệt hoa thường), kèm giảng viên. */
    @Query("select c from Course c left join fetch c.teacher where c.code = :code")
    List<Course> findByCodeWithTeacher(@Param("code") String code);

    boolean existsByEnrollKey(String enrollKey);

    /** Đã có khóa học cùng mã + nhóm (so sánh không phân biệt hoa thường theo collation của bảng). */
    boolean existsByCodeAndGroupName(String code, String groupName);

    boolean existsByCodeAndGroupNameAndIdNot(String code, String groupName, Long id);

    /** Admin: mọi khóa học, mới nhất trước. */
    @Query("select c from Course c left join fetch c.teacher order by c.createdAt desc")
    List<Course> findAllNewestFirst();

    @Query("select c from Course c left join fetch c.teacher where c.teacher.id = :teacherId order by c.createdAt desc")
    List<Course> findByTeacherNewestFirst(@Param("teacherId") long teacherId);
}
