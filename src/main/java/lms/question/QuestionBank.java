package lms.question;

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

/** Ngân hàng câu hỏi của một giảng viên; câu hỏi nằm ở BankQuestion (bank_id). */
@Entity
@Table(name = "question_banks")
public class QuestionBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "owner_id", nullable = false, updatable = false)
    private Long ownerId;

    /** Mã học phần (courses.code); NULL = chưa gắn môn học. */
    @Column(name = "subject_code", length = 32)
    private String subjectCode;

    /** Tên môn lúc chọn (tên khóa học của học phần đó). */
    @Column(name = "subject_name", length = 200)
    private String subjectName;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected QuestionBank() {
        // JPA
    }

    public QuestionBank(long ownerId, String name, String description, String subjectCode, String subjectName) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
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

    /** Thêm/sửa/xóa câu hỏi cũng tính là cập nhật ngân hàng (danh sách sắp theo lần sửa gần nhất). */
    public void touch() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void update(String name, String description, String subjectCode, String subjectName) {
        this.name = name;
        this.description = description;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
