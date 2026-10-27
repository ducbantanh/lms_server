package lms.realtime;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Một sự kiện realtime chờ server web gửi qua WebSocket (do process khác ghi, vd. máy chấm riêng). */
@Entity
@Table(name = "realtime_outbox")
public class RealtimeOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false, updatable = false, length = 64)
    private String type;

    /** data của RealtimeEvent dạng JSON. */
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected RealtimeOutbox() {
        // JPA
    }

    public RealtimeOutbox(long userId, String type, String payload) {
        this.userId = userId;
        this.type = type;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
