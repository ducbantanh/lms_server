package lms.quiz;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;

import lms.common.RandomTokens;

/** Câu hỏi của một bài trắc nghiệm: bản chụp câu trong ngân hàng lúc tạo bài, không sửa sau đó. */
@Entity
@Table(name = "assessment_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "assessment_id", nullable = false, updatable = false)
    private Long assessmentId;

    @Column(nullable = false, updatable = false)
    private int position;

    @Column(nullable = false, updatable = false)
    private String body;

    @Column(nullable = false, updatable = false)
    private boolean multiple;

    @Column(length = 2000, updatable = false)
    private String explanation;

    /** Câu gốc trong ngân hàng; NULL khi câu gốc đã bị xóa. */
    @Column(name = "source_question_id")
    private Long sourceQuestionId;

    @ElementCollection
    @CollectionTable(name = "assessment_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "position")
    @BatchSize(size = 200)
    private List<QuizOption> options = new ArrayList<>();

    protected QuizQuestion() {
        // JPA
    }

    public QuizQuestion(long assessmentId, int position, String body, boolean multiple, String explanation,
            Long sourceQuestionId, List<QuizOption> options) {
        this.assessmentId = assessmentId;
        this.position = position;
        this.body = body;
        this.multiple = multiple;
        this.explanation = explanation;
        this.sourceQuestionId = sourceQuestionId;
        this.options.addAll(options);
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public int getPosition() {
        return position;
    }

    public String getBody() {
        return body;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<QuizOption> getOptions() {
        return options;
    }
}
