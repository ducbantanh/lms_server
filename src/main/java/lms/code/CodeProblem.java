package lms.code;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Một câu của bài lập trình: tiêu đề, đề, giới hạn mỗi test, số lần nộp; bộ test ở CodeTest. */
@Entity
@Table(name = "code_problems")
public class CodeProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "assessment_id", nullable = false, updatable = false)
    private Long assessmentId;

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

    protected CodeProblem() {
        // JPA
    }

    public CodeProblem(long assessmentId) {
        this.assessmentId = assessmentId;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
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
}
