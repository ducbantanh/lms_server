package lms.proctor;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Một dòng nhật ký phiên thi (ProctorRules: loại sự kiện, có tính vi phạm không). */
@Entity
@Table(name = "proctor_events")
public class ProctorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, updatable = false)
    private Long sessionId;

    @Column(nullable = false, updatable = false, length = 32)
    private String type;

    @Column(nullable = false, updatable = false)
    private boolean counted;

    @Column(name = "duration_ms", updatable = false)
    private Long durationMs;

    @Column(updatable = false, length = 300)
    private String detail;

    @Column(updatable = false, length = 45)
    private String ip;

    /** Giám thị khóa / mở khóa / cho đổi máy; null = sinh viên hoặc hệ thống. */
    @Column(name = "actor_id", updatable = false)
    private Long actorId;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    protected ProctorEvent() {
        // JPA
    }

    public ProctorEvent(long sessionId, String type, boolean counted, Long durationMs, String detail, String ip,
            Long actorId, Instant occurredAt) {
        this.sessionId = sessionId;
        this.type = type;
        this.counted = counted;
        this.durationMs = durationMs;
        this.detail = detail == null || detail.length() <= 300 ? detail : detail.substring(0, 300);
        this.ip = ip;
        this.actorId = actorId;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getType() {
        return type;
    }

    public boolean isCounted() {
        return counted;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public String getDetail() {
        return detail;
    }

    public String getIp() {
        return ip;
    }

    public Long getActorId() {
        return actorId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
