package lms.proctor;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/**
 * Phiên thi của một sinh viên ở một bài kiểm tra có giám sát: gắn với phiên đăng nhập (máy) lúc vào phòng thi,
 * đếm vi phạm, bị khóa khi đủ số lần (giám thị mở lại), kết thúc khi nộp bài.
 */
@Entity
@Table(name = "proctor_sessions")
public class ProctorSession {

    public static final int ACTIVE = 0;
    public static final int LOCKED = 1;
    public static final int FINISHED = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "assessment_id", nullable = false, updatable = false)
    private Long assessmentId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    /** Phiên đăng nhập đang làm bài; null = giám thị vừa cho đổi máy, lần vào phòng thi kế tiếp gắn máy mới. */
    @Column(name = "auth_session_id")
    private Long authSessionId;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private int violations;

    @Column(name = "start_ip", length = 45)
    private String startIp;

    @Column(name = "last_ip", length = 45)
    private String lastIp;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(nullable = false)
    private boolean fullscreen;

    @Column(name = "away_since")
    private Instant awaySince;

    @Column(name = "lock_reason", length = 200)
    private String lockReason;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    protected ProctorSession() {
        // JPA
    }

    public ProctorSession(long assessmentId, long courseId, long userId, long authSessionId, String ip,
            String userAgent, Instant now) {
        this.assessmentId = assessmentId;
        this.courseId = courseId;
        this.userId = userId;
        this.authSessionId = authSessionId;
        this.status = ACTIVE;
        this.startIp = ip;
        this.lastIp = ip;
        this.userAgent = userAgent;
        this.startedAt = now;
        this.lastSeenAt = now;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
    }

    /** Vào phòng thi từ máy mới sau khi giám thị cho đổi máy. */
    public void bind(long authSessionId, String ip, String userAgent, Instant now) {
        this.authSessionId = authSessionId;
        this.lastIp = ip;
        this.userAgent = userAgent;
        this.lastSeenAt = now;
        this.awaySince = null;
    }

    public void releaseDevice() {
        this.authSessionId = null;
    }

    public boolean isBoundTo(long authSessionId) {
        return this.authSessionId != null && this.authSessionId == authSessionId;
    }

    public void addViolation() {
        violations++;
    }

    public void lock(String reason, Instant now) {
        this.status = LOCKED;
        this.lockReason = reason;
        this.lockedAt = now;
    }

    public void unlock() {
        this.status = ACTIVE;
        this.lockReason = null;
        this.lockedAt = null;
    }

    public void finish(Instant now) {
        this.status = FINISHED;
        this.finishedAt = now;
        this.awaySince = null;
    }

    public void seen(String ip, Instant now) {
        this.lastIp = ip;
        this.lastSeenAt = now;
    }

    /** Trạng thái trình duyệt báo ở nhịp gần nhất; null = không đổi. */
    public void clientState(Boolean away, Boolean fullscreen, Instant now) {
        if (away != null) {
            if (!away) {
                this.awaySince = null;
            } else if (this.awaySince == null) {
                this.awaySince = now;
            }
        }
        if (fullscreen != null) {
            this.fullscreen = fullscreen;
        }
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

    public Long getCourseId() {
        return courseId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAuthSessionId() {
        return authSessionId;
    }

    public int getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == ACTIVE;
    }

    public boolean isLocked() {
        return status == LOCKED;
    }

    public boolean isFinished() {
        return status == FINISHED;
    }

    public int getViolations() {
        return violations;
    }

    public String getStartIp() {
        return startIp;
    }

    public String getLastIp() {
        return lastIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public Instant getAwaySince() {
        return awaySince;
    }

    public String getLockReason() {
        return lockReason;
    }

    public Instant getLockedAt() {
        return lockedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }
}
