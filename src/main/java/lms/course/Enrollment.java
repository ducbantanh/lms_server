package lms.course;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/** Sinh viên ghi danh vào khóa học. Khóa chính (course_id, user_id). */
@Entity
@Table(name = "enrollments")
@IdClass(Enrollment.Key.class)
public class Enrollment {

    public static final int METHOD_PASTE = 0;
    public static final int METHOD_PICK = 1;
    public static final int METHOD_KEY = 2;

    public record Key(Long courseId, Long userId) implements Serializable {
        public Key() {
            this(null, null);
        }
    }

    @Id
    @Column(name = "course_id")
    private Long courseId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    /** 0 dán MSSV, 1 chọn từ danh sách, 2 nhập mã tham gia. */
    @Column(nullable = false)
    private int method;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;

    protected Enrollment() {
        // JPA
    }

    public Enrollment(long courseId, long userId, int method) {
        this.courseId = courseId;
        this.userId = userId;
        this.method = method;
    }

    @PrePersist
    void onCreate() {
        enrolledAt = Instant.now();
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getMethod() {
        return method;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }
}
