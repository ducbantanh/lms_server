package lms.quiz;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, QuizAnswer.Key> {

    List<QuizAnswer> findBySubmissionId(long submissionId);

    List<QuizAnswer> findBySubmissionIdIn(Collection<Long> submissionIds);

    /**
     * Đổi câu trả lời một câu: xóa các đáp án đã chọn rồi thêm lại. Không clear persistence context (bài nộp
     * đang sửa trong cùng transaction); người gọi không nạp QuizAnswer trước khi xóa.
     */
    @Modifying(flushAutomatically = true)
    @Query("delete from QuizAnswer a where a.submissionId = :submissionId and a.questionId = :questionId")
    void deleteAnswer(@Param("submissionId") long submissionId, @Param("questionId") long questionId);
}
