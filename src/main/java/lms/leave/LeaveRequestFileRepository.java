package lms.leave;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaveRequestFileRepository extends JpaRepository<LeaveRequestFile, Long> {

    Optional<LeaveRequestFile> findByPublicId(String publicId);

    List<LeaveRequestFile> findByRequestIdIn(Collection<Long> requestIds);

    List<LeaveRequestFile> findByRequestId(long requestId);

    // File trên đĩa của các đơn sắp bị xóa theo (FK cascade): người xóa lấy khóa trước rồi xóa file sau khi commit

    @Query("""
            select f.storageKey from LeaveRequestFile f, LeaveRequest r
            where r.id = f.requestId and r.courseId = :courseId
            """)
    List<String> storageKeysByCourse(@Param("courseId") long courseId);

    @Query("""
            select f.storageKey from LeaveRequestFile f, LeaveRequest r
            where r.id = f.requestId and r.meetingId in :meetingIds
            """)
    List<String> storageKeysByMeetings(@Param("meetingIds") Collection<Long> meetingIds);

    /** Cùng điều kiện với ClassMeetingRepository.deleteUnattendedFrom. */
    @Query("""
            select f.storageKey from LeaveRequestFile f, LeaveRequest r, ClassMeeting m
            where r.id = f.requestId and m.id = r.meetingId and m.scheduleId = :scheduleId
              and m.date >= :from and m.attendanceSessionId is null
            """)
    List<String> storageKeysOfUnattendedFrom(@Param("scheduleId") long scheduleId, @Param("from") LocalDate from);
}
