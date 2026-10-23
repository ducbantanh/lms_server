package lms.attendance;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Một buổi điểm danh của khóa học. qrSecret ký mã QR của buổi (AttendanceQr), không trả ra API. */
@Entity
@Table(name = "attendance_sessions")
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "qr_secret", nullable = false, updatable = false, length = 43)
    private String qrSecret;

    @Column(name = "qr_open", nullable = false)
    private boolean qrOpen;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AttendanceSession() {
        // JPA
    }

    public AttendanceSession(long courseId, String title, boolean qrOpen, long createdBy) {
        this.courseId = courseId;
        this.title = title;
        this.qrOpen = qrOpen;
        this.createdBy = createdBy;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        if (qrSecret == null) {
            qrSecret = RandomTokens.urlSafe(32);
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQrSecret() {
        return qrSecret;
    }

    public boolean isQrOpen() {
        return qrOpen;
    }

    public void setQrOpen(boolean qrOpen) {
        this.qrOpen = qrOpen;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
