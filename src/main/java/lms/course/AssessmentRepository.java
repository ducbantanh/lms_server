package lms.course;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByCourseIdOrderByCreatedAt(long courseId);

    List<Assessment> findByCourseIdInOrderByCreatedAt(Collection<Long> courseIds);

    Optional<Assessment> findByPublicIdAndCourseId(String publicId, long courseId);

    Optional<Assessment> findByPublicId(String publicId);

    /** Một file có thể là đề của nhiều bài; dùng khi kiểm tra quyền tải đề. */
    List<Assessment> findByPaperFileId(long paperFileId);

    /** Số câu của bài lập trình (lms.code.CodeProblem). */
    @Query("select count(p) from CodeProblem p where p.assessmentId = :assessmentId")
    long countCodeProblems(@Param("assessmentId") long assessmentId);

    /** Số câu theo từng bài lập trình: [assessmentId, count]. */
    @Query("select p.assessmentId, count(p) from CodeProblem p where p.assessmentId in :ids group by p.assessmentId")
    List<Object[]> countCodeProblems(@Param("ids") Collection<Long> assessmentIds);

    /** Số bài một loại theo từng khóa học: [courseId, count]. */
    @Query("select a.courseId, count(a) from Assessment a where a.courseId in :courseIds and a.type = :type"
            + " group by a.courseId")
    List<Object[]> countByType(@Param("courseIds") Collection<Long> courseIds, @Param("type") int type);
}
