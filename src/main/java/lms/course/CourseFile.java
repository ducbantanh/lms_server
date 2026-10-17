package lms.course;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/** Metadata tài liệu; nội dung nằm trên đĩa tại FileStorage theo storageKey. */
@Entity
@Table(name = "course_files")
public class CourseFile {

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

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long size;

    @Column(nullable = false)
    private boolean hidden;

    @Column(name = "storage_key", nullable = false, updatable = false, length = 43)
    private String storageKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CourseFile() {
        // JPA
    }

    public CourseFile(long courseId, Long sectionId, String name, String contentType, long size, boolean hidden,
            String storageKey) {
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.hidden = hidden;
        this.storageKey = storageKey;
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

    public Long getSectionId() {
        return sectionId;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
