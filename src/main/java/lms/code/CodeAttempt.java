package lms.code;

import java.time.Instant;
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

/**
 * Một lần chạy thử (KIND_RUN: chỉ test mẫu) hoặc nộp bài (KIND_SUBMIT: mọi test) một câu của sinh viên; kết quả là
 * số test đúng / tổng số test, không có điểm.
 * Vòng đời: QUEUED → RUNNING (CodeJudgeWorker giữ bài bằng claimToken) → DONE; chấm lại thì về QUEUED.
 */
@Entity
@Table(name = "code_attempts")
public class CodeAttempt {

    public static final int KIND_RUN = 0;
    public static final int KIND_SUBMIT = 1;

    public static final int QUEUED = 0;
    public static final int RUNNING = 1;
    public static final int DONE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "assessment_id", nullable = false, updatable = false)
    private Long assessmentId;

    @Column(name = "problem_id", nullable = false, updatable = false)
    private Long problemId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false, updatable = false)
    private int kind;

    @Column(nullable = false, updatable = false, length = 16)
    private String language;

    @Column(nullable = false, updatable = false)
    private String source;

    @Column(nullable = false)
    private int status;

    @Column(name = "claim_token", length = 22)
    private String claimToken;

    /** Số lần đã tự chấm lại vì lỗi máy chấm (SE); retryAt: chưa tới giờ này thì worker chưa nhận lại. */
    @Column(name = "judge_retries", nullable = false)
    private int judgeRetries;

    @Column(name = "retry_at")
    private Instant retryAt;

    @Column(length = 8)
    private String verdict;

    private Integer passed;

    private Integer total;

    @Column(name = "compile_output")
    private String compileOutput;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "judged_at")
    private Instant judgedAt;

    @ElementCollection
    @CollectionTable(name = "code_attempt_results", joinColumns = @JoinColumn(name = "attempt_id"))
    @OrderColumn(name = "position")
    @BatchSize(size = 100)
    private List<CodeResult> results = new ArrayList<>();

    protected CodeAttempt() {
        // JPA
    }

    public CodeAttempt(long assessmentId, long problemId, long userId, int kind, CodeLanguage language,
            String source) {
        this.assessmentId = assessmentId;
        this.problemId = problemId;
        this.userId = userId;
        this.kind = kind;
        this.language = language.code();
        this.source = source;
        this.status = QUEUED;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    /** Worker nhận bài; token dùng để bỏ kết quả nếu bài bị đưa về hàng đợi (chấm lại) trong lúc chạy. */
    public String claim() {
        status = RUNNING;
        claimToken = RandomTokens.publicId();
        return claimToken;
    }

    public void finish(CodeJudge.Result r, Instant at) {
        status = DONE;
        claimToken = null;
        verdict = r.verdict();
        passed = r.passed();
        total = r.total();
        compileOutput = r.compileOutput();
        judgedAt = at;
        results.clear();
        r.tests().forEach(t -> results.add(new CodeResult(t.verdict(), t.timeMs(), t.output(), t.error())));
    }

    /** Lỗi máy chấm: đưa về hàng đợi, worker nhận lại từ retryAt; kết quả lần lỗi không lưu. */
    public void retryAfterSystemError(Instant retryAt) {
        status = QUEUED;
        claimToken = null;
        judgeRetries++;
        this.retryAt = retryAt;
    }

    /** Đưa về hàng đợi để chấm lại (bộ test đổi, giảng viên bấm chấm lại); lượt tự chấm lại tính lại từ đầu. */
    public void requeue() {
        status = QUEUED;
        claimToken = null;
        judgeRetries = 0;
        retryAt = null;
        verdict = null;
        passed = null;
        total = null;
        compileOutput = null;
        judgedAt = null;
        results.clear();
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

    public Long getProblemId() {
        return problemId;
    }

    /** Lỗi máy chấm không phải lỗi của sinh viên: không tính là một lần nộp, không tính vào kết quả. */
    public boolean isSystemError() {
        return CodeJudge.SE.equals(verdict);
    }

    public Long getUserId() {
        return userId;
    }

    public int getKind() {
        return kind;
    }

    public boolean isSubmit() {
        return kind == KIND_SUBMIT;
    }

    public CodeLanguage getLanguage() {
        return CodeLanguage.of(language).orElseThrow();
    }

    public String getSource() {
        return source;
    }

    public int getStatus() {
        return status;
    }

    public boolean isDone() {
        return status == DONE;
    }

    public String getClaimToken() {
        return claimToken;
    }

    public int getJudgeRetries() {
        return judgeRetries;
    }

    public String getVerdict() {
        return verdict;
    }

    public Integer getPassed() {
        return passed;
    }

    public Integer getTotal() {
        return total;
    }

    public String getCompileOutput() {
        return compileOutput;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getJudgedAt() {
        return judgedAt;
    }

    public List<CodeResult> getResults() {
        return results;
    }
}
