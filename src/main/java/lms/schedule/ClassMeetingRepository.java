package lms.schedule;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassMeetingRepository extends JpaRepository<ClassMeeting, Long> {

    Optional<ClassMeeting> findByPublicId(String publicId);

    Optional<ClassMeeting> findByPublicIdAndCourseId(String publicId, long courseId);

    List<ClassMeeting> findByScheduleId(long scheduleId);

    boolean existsByRoomId(long roomId);

    /** Buổi của các khóa học trong khoảng ngày [from, to], theo thời gian. */
    @Query("""
            select m from ClassMeeting m
            where m.courseId in :courseIds and m.date between :from and :to
            order by m.date, m.startPeriod, m.id
            """)
    List<ClassMeeting> findInRange(@Param("courseIds") Collection<Long> courseIds, @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    /** Mọi buổi trong khoảng ngày (admin), theo thời gian. */
    @Query("""
            select m from ClassMeeting m
            where m.date between :from and :to
            order by m.date, m.startPeriod, m.id
            """)
    List<ClassMeeting> findAllInRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * Buổi chưa hủy có thể trùng giờ với lịch mới: cùng phòng, cùng khóa học hoặc cùng giảng viên phụ trách,
     * trong các ngày cho trước. Kiểm tra chồng tiết làm ở ScheduleRules.
     */
    @Query("""
            select m from ClassMeeting m, Course c left join c.teacher t
            where c.id = m.courseId and m.cancelled = false and m.date in :dates
              and (m.roomId = :roomId or m.courseId = :courseId or t.id = :teacherId)
            order by m.date, m.startPeriod
            """)
    List<ClassMeeting> findPossibleConflicts(@Param("dates") Collection<LocalDate> dates,
            @Param("roomId") long roomId, @Param("courseId") long courseId, @Param("teacherId") Long teacherId);

    /** Số buổi chưa hủy của từng quy tắc: [scheduleId, count]. */
    @Query("""
            select m.scheduleId, count(m) from ClassMeeting m
            where m.scheduleId in :ids and m.cancelled = false
            group by m.scheduleId
            """)
    List<Object[]> countBySchedule(@Param("ids") Collection<Long> ids);

    /** Buổi chưa hủy của khóa học tới hết ngày cho trước (đánh số "Buổi N"). */
    @Query("""
            select count(m) from ClassMeeting m
            where m.courseId = :courseId and m.cancelled = false
              and (m.date < :date or (m.date = :date and m.startPeriod <= :startPeriod))
            """)
    long countUpTo(@Param("courseId") long courseId, @Param("date") LocalDate date,
            @Param("startPeriod") int startPeriod);

    /**
     * Bỏ các buổi từ ngày cho trước chưa mở điểm danh của một quy tắc (sửa, xóa quy tắc).
     * Không clearAutomatically: người gọi còn dùng Course/CourseSchedule đã nạp (tách khỏi session thì lazy load
     * lỗi và thay đổi trên quy tắc không được lưu); các buổi bị xóa chưa từng được nạp vào session.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
            delete from ClassMeeting m
            where m.scheduleId = :scheduleId and m.date >= :from and m.attendanceSessionId is null
            """)
    int deleteUnattendedFrom(@Param("scheduleId") long scheduleId, @Param("from") LocalDate from);
}
