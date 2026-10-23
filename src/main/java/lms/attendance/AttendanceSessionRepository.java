package lms.attendance;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    Optional<AttendanceSession> findByPublicId(String publicId);

    Optional<AttendanceSession> findByPublicIdAndCourseId(String publicId, long courseId);

    /** Buổi mới nhất trước (danh sách của giảng viên). */
    List<AttendanceSession> findByCourseIdOrderByCreatedAtDescIdDesc(long courseId);

    /** Buổi cũ nhất trước (cột của file CSV, lịch sử của sinh viên). */
    List<AttendanceSession> findByCourseIdOrderByCreatedAtAscIdAsc(long courseId);

    long countByCourseId(long courseId);
}
