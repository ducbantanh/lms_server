package lms.leave;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Đơn xin nghỉ một buổi học của sinh viên; mỗi sinh viên một đơn cho mỗi buổi. */
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "meeting_id", nullable = false, updatable = false)
    private Long meetingId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false)
    private int status = STATUS_PENDING;

    @Column(name = "teacher_note", length = 500)
    private String teacherNote;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected LeaveRequest() {
        // JPA
    }

    public LeaveRequest(long meetingId, long courseId, long userId, String reason) {
        this.meetingId = meetingId;
        this.courseId = courseId;
        this.userId = userId;
        this.reason = reason;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    /** Duyệt (đồng ý / từ chối); duyệt lại được để sửa quyết định. */
    public void review(boolean approve, String note, long teacherId) {
        status = approve ? STATUS_APPROVED : STATUS_REJECTED;
        teacherNote = note;
        reviewedBy = teacherId;
        reviewedAt = Instant.now();
    }

    public boolean isPending() {
        return status == STATUS_PENDING;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }

    public int getStatus() {
        return status;
    }

    public String getTeacherNote() {
        return teacherNote;
    }

    public Long getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
