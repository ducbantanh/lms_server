package lms.code;

import java.nio.charset.StandardCharsets;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/**
 * Một test của bài lập trình. Nội dung test có thể lớn (tới vài MB): chỉ nạp entity khi thêm test; các chỗ
 * khác đọc qua CodeTestRepository (thông tin + bản xem trước, hoặc nội dung từng test khi chấm).
 */
@Entity
@Table(name = "code_tests")
public class CodeTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "problem_id", nullable = false, updatable = false)
    private Long problemId;

    /** Thứ tự, test mẫu đổi bằng CodeTestRepository.updateMeta. */
    @Column(nullable = false, updatable = false)
    private int position;

    /** Tên file trong ZIP (vd. "test05"); null = test soạn tay. */
    @Column(length = 100, updatable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private String input;

    @Column(nullable = false, updatable = false)
    private String expected;

    /** Byte UTF-8. */
    @Column(name = "input_size", nullable = false, updatable = false)
    private long inputSize;

    @Column(name = "expected_size", nullable = false, updatable = false)
    private long expectedSize;

    /** Test mẫu: sinh viên thấy input/output, dùng khi chạy thử. */
    @Column(nullable = false, updatable = false)
    private boolean sample;

    protected CodeTest() {
        // JPA
    }

    public CodeTest(long problemId, int position, String name, String input, String expected, boolean sample) {
        this.problemId = problemId;
        this.position = position;
        this.name = name;
        this.input = input;
        this.expected = expected;
        this.inputSize = input.getBytes(StandardCharsets.UTF_8).length;
        this.expectedSize = expected.getBytes(StandardCharsets.UTF_8).length;
        this.sample = sample;
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

    public long getInputSize() {
        return inputSize;
    }

    public long getExpectedSize() {
        return expectedSize;
    }
}
