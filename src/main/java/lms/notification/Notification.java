package lms.notification;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Nội dung một thông báo của khóa học; người nhận và trạng thái đã đọc ở NotificationRecipient. */
@Entity
@Table(name = "notifications")
public class Notification {

    public static final int CATEGORY_EXAM = 0;
    public static final int CATEGORY_LAB = 1;
    public static final int CATEGORY_PRACTICE = 2;
    public static final int CATEGORY_CONTENT = 3;
    public static final int CATEGORY_ANNOUNCEMENT = 4;
    /** Lịch học thay đổi: buổi bù, hủy, dời (do admin xếp). */
    public static final int CATEGORY_SCHEDULE = 5;
    /** Kết quả duyệt đơn xin nghỉ (gửi riêng sinh viên viết đơn). */
    public static final int CATEGORY_LEAVE = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(nullable = false, updatable = false)
    private int category;

    @Column(nullable = false, updatable = false, length = 200)
    private String title;

    @Column(updatable = false, length = 2000)
    private String body;

    @Column(name = "assessment_id", updatable = false)
    private Long assessmentId;

    @Column(name = "sender_id", updatable = false)
    private Long senderId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Notification() {
        // JPA
    }

    public Notification(long courseId, int category, String title, String body, Long assessmentId, Long senderId) {
        this.courseId = courseId;
        this.category = category;
        this.title = title;
        this.body = body;
        this.assessmentId = assessmentId;
        this.senderId = senderId;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public int getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
