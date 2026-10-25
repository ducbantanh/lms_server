package lms.code;

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

/**
 * Một câu lập trình: tiêu đề, đề, giới hạn mỗi test, số lần nộp; bộ test ở CodeTest. Câu của bài có assessmentId;
 * câu trong kho bài lập trình của giảng viên có ownerId (assessmentId null) và môn học để lọc.
 */
@Entity
@Table(name = "code_problems")
public class CodeProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "assessment_id", updatable = false)
    private Long assessmentId;

    /** Chủ của câu trong kho; null với câu của bài. */
    @Column(name = "owner_id", updatable = false)
    private Long ownerId;

    @Column(name = "subject_code", length = 32)
    private String subjectCode;

    @Column(name = "subject_name", length = 200)
    private String subjectName;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String statement;

    /** Mỗi test; Java, Python được nhân thêm (CodeLanguage.timeFactor). */
    @Column(name = "time_limit_ms", nullable = false)
    private int timeLimitMs;

    @Column(name = "memory_limit_mb", nullable = false)
    private int memoryLimitMb;

    /** Số lần nộp tối đa (chạy thử không tính); null = không giới hạn. */
    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    /** Lần sửa câu gần nhất (kể cả bộ test); câu cũ trước V23 là null. */
    @Column(name = "updated_at")
    private Instant updatedAt;

    protected CodeProblem() {
        // JPA
    }

    public CodeProblem(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    /** Câu mới trong kho bài lập trình của giảng viên. */
    public static CodeProblem library(long ownerId) {
        CodeProblem p = new CodeProblem();
        p.ownerId = ownerId;
        return p;
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

    public void update(int position, String title, String statement, int timeLimitMs, int memoryLimitMb,
            Integer maxAttempts) {
        this.position = position;
        this.title = title;
        this.statement = statement;
        this.timeLimitMs = timeLimitMs;
        this.memoryLimitMb = memoryLimitMb;
        this.maxAttempts = maxAttempts;
    }

    public void setSubject(String code, String name) {
        this.subjectCode = code;
        this.subjectName = name;
    }

    /** Sửa riêng bộ test cũng tính là sửa câu (PreUpdate chỉ chạy khi cột của câu đổi). */
    public void touch() {
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

    public Long getOwnerId() {
        return ownerId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getStatement() {
        return statement;
    }

    public int getTimeLimitMs() {
        return timeLimitMs;
    }

    public int getMemoryLimitMb() {
        return memoryLimitMb;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
