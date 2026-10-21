package lms.submission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Long> {

    List<SubmissionFile> findBySubmissionIdOrderByCreatedAt(long submissionId);

    /** File của nhiều bài nộp một lần, cho danh sách bài nộp của giảng viên. */
    List<SubmissionFile> findBySubmissionIdInOrderByCreatedAt(Collection<Long> submissionIds);

    Optional<SubmissionFile> findByPublicId(String publicId);

    long countBySubmissionId(long submissionId);

    /** Số file theo từng bài nộp: [submissionId, count]. */
    @Query("select f.submissionId, count(f) from SubmissionFile f where f.submissionId in :ids group by f.submissionId")
    List<Object[]> countBySubmissionIds(@Param("ids") Collection<Long> ids);

    /** Xóa bài: DB xóa dây chuyền bài nộp, service xóa file trên đĩa theo các key này sau khi commit. */
    @Query("select f.storageKey from SubmissionFile f, Submission s"
            + " where f.submissionId = s.id and s.assessmentId = :assessmentId")
    List<String> storageKeysByAssessment(@Param("assessmentId") long assessmentId);

    /** Xóa khóa học: như trên cho mọi bài của khóa học. */
    @Query("select f.storageKey from SubmissionFile f, Submission s, Assessment a"
            + " where f.submissionId = s.id and s.assessmentId = a.id and a.courseId = :courseId")
    List<String> storageKeysByCourse(@Param("courseId") long courseId);
}
