package lms.code;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeProblemRepository extends JpaRepository<CodeProblem, Long> {

    List<CodeProblem> findByAssessmentIdOrderByPosition(long assessmentId);

    Optional<CodeProblem> findByPublicIdAndAssessmentId(String publicId, long assessmentId);

    /** Kho bài lập trình của một giảng viên, câu sửa gần nhất trước. */
    List<CodeProblem> findByOwnerIdOrderByUpdatedAtDescIdDesc(long ownerId);

    Optional<CodeProblem> findByPublicIdAndOwnerId(String publicId, long ownerId);

    List<CodeProblem> findByPublicIdInAndOwnerId(Collection<String> publicIds, long ownerId);
}
