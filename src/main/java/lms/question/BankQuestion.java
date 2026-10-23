package lms.question;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;

import lms.common.RandomTokens;

/** Câu hỏi trắc nghiệm trong ngân hàng: nội dung, kiểu chọn một/nhiều, mức độ và các đáp án (đúng/sai). */
@Entity
@Table(name = "bank_questions")
public class BankQuestion {

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "bank_id", nullable = false, updatable = false)
    private Long bankId;

    @Column(nullable = false)
    private String body;

    /** true = sinh viên được chọn nhiều đáp án (có thể chỉ một đáp án đúng). */
    @Column(nullable = false)
    private boolean multiple;

    @Column(nullable = false)
    private int difficulty;

    @Column(length = 2000)
    private String explanation;

    // Mở một ngân hàng là tải cả trăm câu: nạp đáp án theo lô thay vì mỗi câu một truy vấn
    @ElementCollection
    @CollectionTable(name = "bank_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "position")
    @BatchSize(size = 200)
    private List<BankOption> options = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BankQuestion() {
        // JPA
    }

    public BankQuestion(long bankId, String body, boolean multiple, int difficulty, String explanation,
            List<BankOption> options) {
        this.bankId = bankId;
        update(body, multiple, difficulty, explanation, options);
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

    /** Sửa câu hỏi: thay toàn bộ danh sách đáp án. */
    public void update(String body, boolean multiple, int difficulty, String explanation, List<BankOption> options) {
        this.body = body;
        this.multiple = multiple;
        this.difficulty = difficulty;
        this.explanation = explanation;
        this.options.clear();
        this.options.addAll(options);
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Long getBankId() {
        return bankId;
    }

    public String getBody() {
        return body;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<BankOption> getOptions() {
        return options;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
