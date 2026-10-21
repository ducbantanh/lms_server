package lms.progress;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** Sinh viên đã đánh dấu học xong một bài học. Khóa chính (lesson_id, user_id); ghi bằng INSERT IGNORE. */
@Entity
@Table(name = "lesson_completions")
@IdClass(LessonCompletion.Key.class)
public class LessonCompletion {

    public record Key(Long lessonId, Long userId) implements Serializable {
        public Key() {
            this(null, null);
        }
    }

    @Id
    @Column(name = "lesson_id")
    private Long lessonId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "completed_at", nullable = false, updatable = false)
    private Instant completedAt;

    protected LessonCompletion() {
        // JPA
    }

    public Long getLessonId() {
        return lessonId;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
