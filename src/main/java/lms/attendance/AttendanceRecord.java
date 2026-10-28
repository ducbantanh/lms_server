package lms.attendance;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** Trạng thái điểm danh của một sinh viên trong một buổi. Khóa chính (session_id, user_id); không có dòng = vắng. */
@Entity
@Table(name = "attendance_records")
@IdClass(AttendanceRecord.Key.class)
public class AttendanceRecord {

    public static final int STATUS_PRESENT = 0;
    public static final int STATUS_LATE = 1;
    public static final int STATUS_EXCUSED = 2;

    public static final int METHOD_QR = 0;
    public static final int METHOD_MANUAL = 1;

    public record Key(Long sessionId, Long userId) implements Serializable {
        public Key() {
            this(null, null);
        }
    }

    @Id
    @Column(name = "session_id")
    private Long sessionId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private int method;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    @Column(name = "marked_by")
    private Long markedBy;

    @Column(length = 45)
    private String ip;

    protected AttendanceRecord() {
        // JPA
    }

    /** Dòng quét QR do AttendanceRecordRepository.insertScanned thêm (luôn là có mặt). */
    public static AttendanceRecord manual(long sessionId, long userId, int status, long teacherId) {
        AttendanceRecord r = new AttendanceRecord();
        r.sessionId = sessionId;
        r.userId = userId;
        r.markManually(status, teacherId);
        return r;
    }

    /** Giảng viên đổi trạng thái: thành điểm danh thủ công, thời điểm là lúc đổi; IP lúc quét không còn ý nghĩa. */
    public void markManually(int status, long teacherId) {
        this.status = status;
        this.method = METHOD_MANUAL;
        this.checkedAt = Instant.now();
        this.markedBy = teacherId;
        this.ip = null;
    }

    public static boolean isValidStatus(int status) {
        return status == STATUS_PRESENT || status == STATUS_LATE || status == STATUS_EXCUSED;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getStatus() {
        return status;
    }

    public int getMethod() {
        return method;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }

    public Long getMarkedBy() {
        return markedBy;
    }

    public String getIp() {
        return ip;
    }
}
