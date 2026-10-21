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

/** Bài kiểm tra (luyện tập / thực hành / thi), có thể kèm file đề PDF và khung giờ mở. */
@Entity
@Table(name = "assessments")
public class Assessment {

    public static final int TYPE_PRACTICE = 0;
    public static final int TYPE_LAB = 1;
    public static final int TYPE_EXAM = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(nullable = false)
    private int type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "paper_file_id")
    private Long paperFileId;

    /** NULL = mở ngay. */
    @Column(name = "opens_at")
    private Instant opensAt;

    /** NULL = không hạn. */
    @Column(name = "closes_at")
    private Instant closesAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Assessment() {
        // JPA
    }

    public Assessment(long courseId, int type, String title, String description, Long paperFileId,
            Instant opensAt, Instant closesAt) {
        this.courseId = courseId;
        this.type = type;
        this.title = title;
        this.description = description;
        this.paperFileId = paperFileId;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    /** Sửa bài; loại bài và đề không đổi được (đổi đề thì xóa và tạo bài mới). */
    public void update(String title, String description, Instant opensAt, Instant closesAt) {
        this.title = title;
        this.description = description;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    /** Bài kiểm tra đã kết thúc là hồ sơ thi: không sửa, không xóa. Bài tập/thực hành sửa lúc nào cũng được. */
    public boolean isLocked(Instant now) {
        return type == TYPE_EXAM && isClosed(now);
    }

    public boolean isOpened(Instant now) {
        return opensAt == null || !opensAt.isAfter(now);
    }

    public boolean isClosed(Instant now) {
        return isOpened(now) && closesAt != null && closesAt.isBefore(now);
    }

    /** upcoming / open / closed, khớp ASSESSMENT_STATUS ở client. */
    public String statusAt(Instant now) {
        if (!isOpened(now)) {
            return "upcoming";
        }
        return isClosed(now) ? "closed" : "open";
    }

    public boolean isDeletable(Instant now) {
        return !isLocked(now);
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

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getPaperFileId() {
        return paperFileId;
    }

    public Instant getOpensAt() {
        return opensAt;
    }

    public Instant getClosesAt() {
        return closesAt;
    }
}
