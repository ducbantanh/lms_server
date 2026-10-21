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

    /** Tài liệu của khóa học (nằm trong mục/bài học). */
    public static final int KIND_MATERIAL = 0;
    /** Đề tải lên riêng cho một bài; chỉ truy cập qua bài đó. */
    public static final int KIND_PAPER = 1;

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

    /** NULL = tài liệu nằm trực tiếp trong mục; khác NULL thì sectionId luôn bằng mục của bài. */
    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(nullable = false, updatable = false)
    private int kind = KIND_MATERIAL;

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

    public CourseFile(long courseId, Long sectionId, Long lessonId, String name, String contentType, long size,
            boolean hidden, String storageKey) {
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.lessonId = lessonId;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.hidden = hidden;
        this.storageKey = storageKey;
    }

    /** Đề riêng của một bài: ẩn, không thuộc mục hay bài học nào. */
    public static CourseFile paper(long courseId, String name, long size, String storageKey) {
        CourseFile f = new CourseFile(courseId, null, null, name, CourseContentService.PDF, size, true, storageKey);
        f.kind = KIND_PAPER;
        return f;
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

    public Long getLessonId() {
        return lessonId;
    }

    public boolean isPaper() {
        return kind == KIND_PAPER;
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
