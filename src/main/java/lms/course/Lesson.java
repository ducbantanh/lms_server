package lms.course;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Bài học trong một mục/tuần: nội dung văn bản + tài liệu đính kèm (course_files.lesson_id). */
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    /** NULL = chưa phân mục. */
    @Column(name = "section_id")
    private Long sectionId;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false, length = 200)
    private String title;

    /** Văn bản thuần; NULL = bài chỉ có tài liệu đính kèm. */
    @Column
    private String body;

    @Column(nullable = false)
    private boolean hidden;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Lesson() {
        // JPA
    }

    public Lesson(long courseId, Long sectionId, int position, String title, String body, boolean hidden) {
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.position = position;
        this.title = title;
        this.body = body;
        this.hidden = hidden;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
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

    public Long getSectionId() {
        return sectionId;
    }

    /** Chuyển sang mục khác thì đặt cuối mục mới. */
    public void moveTo(Long sectionId, int position) {
        this.sectionId = sectionId;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    /** Sắp xếp lại trong cùng mục. */
    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
