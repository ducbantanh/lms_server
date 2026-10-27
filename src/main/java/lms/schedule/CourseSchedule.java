package lms.schedule;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lms.common.RandomTokens;

/**
 * Quy tắc lặp hằng tuần của một khóa học: thứ, tiết bắt đầu, số tiết, phòng, khoảng ngày.
 * Các buổi cụ thể (ClassMeeting) được sinh sẵn từ quy tắc khi tạo/sửa.
 */
@Entity
@Table(name = "course_schedules")
public class CourseSchedule {

    public static final int KIND_THEORY = 0;
    public static final int KIND_PRACTICE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    /** 1 thứ 2 … 7 chủ nhật (DayOfWeek.getValue()). */
    @Column(nullable = false)
    private int weekday;

    @Column(name = "start_period", nullable = false)
    private int startPeriod;

    @Column(nullable = false)
    private int periods;

    @Column(nullable = false)
    private int kind;

    @Column(name = "starts_on", nullable = false)
    private LocalDate startsOn;

    @Column(name = "ends_on", nullable = false)
    private LocalDate endsOn;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CourseSchedule() {
        // JPA
    }

    public CourseSchedule(long courseId, long createdBy) {
        this.courseId = courseId;
        this.createdBy = createdBy;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    public static boolean isValidKind(int kind) {
        return kind == KIND_THEORY || kind == KIND_PRACTICE;
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

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(int startPeriod) {
        this.startPeriod = startPeriod;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public LocalDate getStartsOn() {
        return startsOn;
    }

    public void setStartsOn(LocalDate startsOn) {
        this.startsOn = startsOn;
    }

    public LocalDate getEndsOn() {
        return endsOn;
    }

    public void setEndsOn(LocalDate endsOn) {
        this.endsOn = endsOn;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
