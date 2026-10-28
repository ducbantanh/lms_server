package lms.leave;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    Optional<LeaveRequest> findByPublicId(String publicId);

    boolean existsByMeetingIdAndUserId(long meetingId, long userId);

    /** Đơn của một sinh viên ở các buổi (lịch của sinh viên). */
    List<LeaveRequest> findByUserIdAndMeetingIdIn(long userId, Collection<Long> meetingIds);

    List<LeaveRequest> findByMeetingIdAndStatus(long meetingId, int status);

    /** Đơn của khóa học: chờ duyệt trước, rồi mới nhất trước. */
    @Query("""
            select r from LeaveRequest r where r.courseId = :courseId
            order by case when r.status = 0 then 0 else 1 end, r.createdAt desc, r.id desc
            """)
    List<LeaveRequest> findForCourse(@Param("courseId") long courseId);

    /** Số đơn chờ duyệt theo buổi: [meetingId, count]. */
    @Query("""
            select r.meetingId, count(r) from LeaveRequest r
            where r.meetingId in :meetingIds and r.status = 0
            group by r.meetingId
            """)
    List<Object[]> countPendingByMeeting(@Param("meetingIds") Collection<Long> meetingIds);
}
