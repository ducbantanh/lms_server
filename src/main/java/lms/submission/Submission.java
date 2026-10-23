package lms.submission;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Bài nộp của một sinh viên cho một bài (kèm điểm chấm thủ công); file nằm ở SubmissionFile. */
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "assessment_id", nullable = false, updatable = false)
    private Long assessmentId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    /** Bài trắc nghiệm: lúc bấm "Bắt đầu"; NULL với bài nộp file. */
    @Column(name = "started_at")
    private Instant startedAt;

    /** NULL = đang làm; khác NULL = đã bấm nộp, không sửa được nữa. */
    @Column(name = "submitted_at")
    private Instant submittedAt;

    /** Điểm thang 10; NULL = chưa chấm. */
    @Column(precision = 4, scale = 2)
    private BigDecimal score;

    /** Điểm máy chấm của bài trắc nghiệm; score vẫn là điểm chính thức (giảng viên sửa được). */
    @Column(name = "auto_score", precision = 4, scale = 2)
    private BigDecimal autoScore;

    @Column(length = 2000)
    private String feedback;

    @Column(name = "graded_at")
    private Instant gradedAt;

    @Column(name = "graded_by")
    private Long gradedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Submission() {
        // JPA
    }

    public Submission(long assessmentId, long userId) {
        this.assessmentId = assessmentId;
        this.userId = userId;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
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

    public Long getUserId() {
        return userId;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void start(Instant at) {
        this.startedAt = at;
    }

    public BigDecimal getAutoScore() {
        return autoScore;
    }

    /** Chấm tự động bài trắc nghiệm; giảng viên đã chấm tay thì giữ điểm của giảng viên. */
    public void autoGrade(BigDecimal score, Instant at) {
        this.autoScore = score;
        if (this.score == null) {
            this.score = score;
            this.gradedAt = at;
            this.gradedBy = null;
        }
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public boolean isSubmitted() {
        return submittedAt != null;
    }

    public void submit(Instant at) {
        this.submittedAt = at;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public Instant getGradedAt() {
        return gradedAt;
    }

    public Long getGradedBy() {
        return gradedBy;
    }

    public boolean isGraded() {
        return score != null;
    }

    public void grade(BigDecimal score, String feedback, long graderId, Instant at) {
        this.score = score;
        this.feedback = feedback;
        this.gradedBy = graderId;
        this.gradedAt = at;
    }

    public void clearGrade() {
        this.score = null;
        this.feedback = null;
        this.gradedBy = null;
        this.gradedAt = null;
    }

    /** Thêm/xóa file cũng tính là cập nhật bài nộp. */
    public void touch() {
        this.updatedAt = Instant.now();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
