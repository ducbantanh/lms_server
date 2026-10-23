package lms.course;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;
import lms.user.User;

@Entity
@Table(name = "courses")
public class Course {

    public static final int FORMAT_SECTIONS = 0;
    public static final int FORMAT_WEEKS = 1;
    public static final int FORMAT_FILES_ONLY = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(nullable = false, length = 32)
    private String code;

    /** Nhóm của học phần (vd. "01"); null = không chia nhóm. Mã + nhóm là duy nhất. */
    @Column(name = "group_name", length = 16)
    private String groupName;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    /** 0 mục tự đặt tên, 1 theo tuần, 2 chỉ danh sách file. */
    @Column(nullable = false)
    private int format = FORMAT_SECTIONS;

    /** NULL = tắt tham gia bằng mã. */
    @Column(name = "enroll_key", length = 16)
    private String enrollKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Course() {
        // JPA
    }

    public Course(String code, String title, User teacher) {
        this.code = code;
        this.title = title;
        this.teacher = teacher;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    /** Admin quản lý mọi khóa học; giảng viên chỉ khóa học mình phụ trách. */
    public boolean isManagedBy(long userId, boolean admin) {
        return admin || (teacher != null && teacher.getId() == userId);
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Mã kèm nhóm cho nơi chỉ hiện một chuỗi (thông báo, bài nộp, nhật ký, tên file): "INT1340 · Nhóm 01".
     * Client dùng cùng định dạng trong courseLabel() (lib/course-utils.ts).
     */
    public String displayCode() {
        return groupName == null ? code : code + " · Nhóm " + groupName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public String getEnrollKey() {
        return enrollKey;
    }

    public void setEnrollKey(String enrollKey) {
        this.enrollKey = enrollKey;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public User getTeacher() {
        return teacher;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
