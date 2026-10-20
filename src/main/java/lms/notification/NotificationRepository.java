package lms.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByPublicId(String publicId);

    /** Lịch sử thông báo của khóa học cho giảng viên, mới nhất trước. */
    List<Notification> findByCourseIdOrderByCreatedAtDescIdDesc(long courseId, Pageable page);
}
