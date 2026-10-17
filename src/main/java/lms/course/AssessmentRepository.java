package lms.course;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByCourseIdOrderByCreatedAt(long courseId);

    Optional<Assessment> findByPublicIdAndCourseId(String publicId, long courseId);

    /** Một file có thể là đề của nhiều bài; dùng khi kiểm tra quyền tải đề. */
    List<Assessment> findByPaperFileId(long paperFileId);
}
