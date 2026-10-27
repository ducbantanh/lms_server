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
 * Một buổi học cụ thể: ngày, tiết, phòng. Sinh từ CourseSchedule (scheduleId) hoặc thêm lẻ (buổi bù, scheduleId null).
 * Dời buổi thì giữ ngày, tiết ban đầu ở original* (chỉ lần dời đầu tiên) để hiện "dời từ ...".
 */
@Entity
@Table(name = "class_meetings")
public class ClassMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false, length = 22)
    private String publicId;

    @Column(name = "course_id", nullable = false, updatable = false)
    private Long courseId;

    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_period", nullable = false)
    private int startPeriod;

    @Column(nullable = false)
    private int periods;

    @Column(nullable = false)
    private int kind;

    @Column(nullable = false)
    private boolean cancelled;

    @Column(name = "original_date")
    private LocalDate originalDate;

    @Column(name = "original_start_period")
    private Integer originalStartPeriod;

    @Column(length = 200)
    private String note;

    @Column(name = "attendance_session_id")
    private Long attendanceSessionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ClassMeeting() {
        // JPA
    }

    public ClassMeeting(long courseId, Long scheduleId, long roomId, LocalDate date, int startPeriod, int periods,
            int kind) {
        this.courseId = courseId;
        this.scheduleId = scheduleId;
        this.roomId = roomId;
        this.date = date;
        this.startPeriod = startPeriod;
        this.periods = periods;
        this.kind = kind;
    }

    @PrePersist
    void onCreate() {
        if (publicId == null) {
            publicId = RandomTokens.publicId();
        }
        createdAt = Instant.now();
    }

    /** Tiết cuối cùng (tính cả tiết bắt đầu). */
    public int endPeriod() {
        return startPeriod + periods - 1;
    }

    /** Đổi ngày/tiết/phòng; lần đầu đổi ngày hoặc tiết thì ghi lại ngày, tiết ban đầu. */
    public void moveTo(LocalDate newDate, int newStartPeriod, int newPeriods, long newRoomId) {
        boolean timeChanged = !newDate.equals(date) || newStartPeriod != startPeriod;
        if (timeChanged && originalDate == null) {
            originalDate = date;
            originalStartPeriod = startPeriod;
        }
        date = newDate;
        startPeriod = newStartPeriod;
        periods = newPeriods;
        roomId = newRoomId;
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

    public Long getScheduleId() {
        return scheduleId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getStartPeriod() {
        return startPeriod;
    }

    public int getPeriods() {
        return periods;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public LocalDate getOriginalDate() {
        return originalDate;
    }

    public Integer getOriginalStartPeriod() {
        return originalStartPeriod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getAttendanceSessionId() {
        return attendanceSessionId;
    }

    public void setAttendanceSessionId(Long attendanceSessionId) {
        this.attendanceSessionId = attendanceSessionId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
