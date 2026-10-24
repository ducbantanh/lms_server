package lms.code;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeProblemRepository extends JpaRepository<CodeProblem, Long> {

    List<CodeProblem> findByAssessmentIdOrderByPosition(long assessmentId);

    Optional<CodeProblem> findByPublicIdAndAssessmentId(String publicId, long assessmentId);
}
