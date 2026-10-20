package lms.submission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByPublicId(String publicId);

    Optional<Submission> findByAssessmentIdAndUserId(Long assessmentId, Long userId);

    List<Submission> findByAssessmentId(Long assessmentId);

    List<Submission> findByUserId(Long userId);

    /** Lấy danh sách submission kèm user: [Submission, User] theo assessmentId. */
    @Query("""
            select s, u from Submission s join User u on u.id = s.userId
            where s.assessmentId = :assessmentId
            order by s.submittedAt desc
            """)
    List<Object[]> findWithUsersByAssessmentId(@Param("assessmentId") Long assessmentId);
}
