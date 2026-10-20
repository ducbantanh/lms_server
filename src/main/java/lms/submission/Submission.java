package lms.submission;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

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

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "storage_key", nullable = false, length = 43)
    private String storageKey;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(length = 2000)
    private String feedback;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Column(name = "graded_at")
    private Instant gradedAt;

    @Column(name = "graded_by")
    private Long gradedBy;

    protected Submission() {
        // JPA
    }

    public Submission(Long assessmentId, Long userId, String fileName, String storageKey,
                      String contentType, long sizeBytes) {
        this.assessmentId = assessmentId;
        this.userId = userId;
        this.fileName = fileName;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        if (submittedAt == null) {
            submittedAt = Instant.now();
        }
    }

    public void updateFile(String fileName, String storageKey, String contentType, long sizeBytes) {
        this.fileName = fileName;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.submittedAt = Instant.now();
    }

    public void grade(BigDecimal score, String feedback, Long gradedBy) {
        this.score = score;
        this.feedback = feedback;
        this.gradedBy = gradedBy;
        this.gradedAt = Instant.now();
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

    public String getFileName() {
        return fileName;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getGradedAt() {
        return gradedAt;
    }

    public Long getGradedBy() {
        return gradedBy;
    }
}
