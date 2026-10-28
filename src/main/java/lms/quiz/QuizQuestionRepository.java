package lms.quiz;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByAssessmentIdOrderByPosition(long assessmentId);

    Optional<QuizQuestion> findByPublicIdAndAssessmentId(String publicId, long assessmentId);

    long countByAssessmentId(long assessmentId);

    /** Số câu theo từng bài: [assessmentId, count]. */
    @Query("select q.assessmentId, count(q) from QuizQuestion q where q.assessmentId in :ids group by q.assessmentId")
    List<Object[]> countByAssessmentIds(@Param("ids") Collection<Long> assessmentIds);
}
