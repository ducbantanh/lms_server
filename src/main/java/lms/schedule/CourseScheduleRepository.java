package lms.schedule;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    Optional<CourseSchedule> findByPublicId(String publicId);

    List<CourseSchedule> findByCourseIdInOrderByWeekdayAscStartPeriodAsc(Collection<Long> courseIds);

    List<CourseSchedule> findAllByOrderByWeekdayAscStartPeriodAsc();

    List<CourseSchedule> findByRoomIdOrderByWeekdayAscStartPeriodAsc(long roomId);

    boolean existsByRoomId(long roomId);
}
