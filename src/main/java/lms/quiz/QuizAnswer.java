package lms.quiz;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** Một đáp án sinh viên đã chọn: (bài nộp, câu hỏi, vị trí đáp án). Câu chưa trả lời thì không có dòng. */
@Entity
@Table(name = "submission_answers")
@IdClass(QuizAnswer.Key.class)
public class QuizAnswer {

    public record Key(Long submissionId, Long questionId, Integer optionPosition) implements Serializable {
        public Key() {
            this(null, null, null);
        }
    }

    @Id
    @Column(name = "submission_id")
    private Long submissionId;

    @Id
    @Column(name = "question_id")
    private Long questionId;

    @Id
    @Column(name = "option_position")
    private Integer optionPosition;

    protected QuizAnswer() {
        // JPA
    }

    public QuizAnswer(long submissionId, long questionId, int optionPosition) {
        this.submissionId = submissionId;
        this.questionId = questionId;
        this.optionPosition = optionPosition;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Integer getOptionPosition() {
        return optionPosition;
    }
}
