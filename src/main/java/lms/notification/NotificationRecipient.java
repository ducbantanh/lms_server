package lms.notification;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.springframework.data.domain.Persistable;

/**
 * Một người nhận thông báo. Khóa chính (notification_id, user_id); readAt NULL = chưa đọc.
 * Persistable để saveAll chèn thẳng, không SELECT từng dòng như khi khóa tự gán.
 */
@Entity
@Table(name = "notification_recipients")
@IdClass(NotificationRecipient.Key.class)
public class NotificationRecipient implements Persistable<NotificationRecipient.Key> {

    public record Key(Long notificationId, Long userId) implements Serializable {
        public Key() {
            this(null, null);
        }
    }

    @Id
    @Column(name = "notification_id")
    private Long notificationId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "read_at")
    private Instant readAt;

    @Transient
    private boolean fresh;

    protected NotificationRecipient() {
        // JPA
    }

    public NotificationRecipient(long notificationId, long userId) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.fresh = true;
    }

    @PostLoad
    @PostPersist
    void markStored() {
        fresh = false;
    }

    @Override
    public Key getId() {
        return new Key(notificationId, userId);
    }

    @Override
    public boolean isNew() {
        return fresh;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getReadAt() {
        return readAt;
    }
}
