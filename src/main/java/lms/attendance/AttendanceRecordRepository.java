package lms.attendance;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, AttendanceRecord.Key> {

    List<AttendanceRecord> findBySessionId(long sessionId);

    List<AttendanceRecord> findBySessionIdIn(Collection<Long> sessionIds);

    List<AttendanceRecord> findBySessionIdAndUserIdIn(long sessionId, Collection<Long> userIds);

    List<AttendanceRecord> findBySessionIdInAndUserId(Collection<Long> sessionIds, long userId);

    /**
     * Số sinh viên theo trạng thái ở từng buổi, chỉ tính người còn ghi danh: [sessionId, status, count].
     * Sinh viên đã rời lớp vẫn giữ dòng điểm danh (ghi danh lại thì hiện lại) nhưng không làm lệch sĩ số.
     */
    @Query("select r.sessionId, r.status, count(r) from AttendanceRecord r, Enrollment e"
            + " where r.sessionId in :sessionIds and e.courseId = :courseId and e.userId = r.userId"
            + " group by r.sessionId, r.status")
    List<Object[]> countByStatus(@Param("courseId") long courseId, @Param("sessionIds") Collection<Long> sessionIds);

    /**
     * Sinh viên quét QR: thêm dòng "có mặt" (status 0, method 0); đã có dòng (quét lại, hai tab cùng lúc,
     * giảng viên đã điểm danh thủ công) thì giữ nguyên. Trả 1 nếu vừa thêm. Giờ UTC như mọi cột DATETIME khác.
     */
    @Modifying
    @Query(nativeQuery = true, value = "insert ignore into attendance_records"
            + " (session_id, user_id, status, method, checked_at, ip)"
            + " values (:sessionId, :userId, 0, 0, utc_timestamp(3), :ip)")
    int insertScanned(@Param("sessionId") long sessionId, @Param("userId") long userId, @Param("ip") String ip);

    @Modifying
    @Query("delete from AttendanceRecord r where r.sessionId = :sessionId and r.userId in :userIds")
    int deleteSome(@Param("sessionId") long sessionId, @Param("userIds") Collection<Long> userIds);
}
