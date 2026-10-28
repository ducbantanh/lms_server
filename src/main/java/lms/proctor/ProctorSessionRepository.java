package lms.proctor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProctorSessionRepository extends JpaRepository<ProctorSession, Long> {

    Optional<ProctorSession> findByAssessmentIdAndUserId(long assessmentId, long userId);

    List<ProctorSession> findByAssessmentId(long assessmentId);

    /** Khóa dòng khi đếm vi phạm / đổi trạng thái: nhịp và sự kiện của cùng phiên có thể tới song song. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ProctorSession s where s.id = :id")
    Optional<ProctorSession> lockById(@Param("id") long id);

    /**
     * Bài kiểm tra có giám sát mà sinh viên đang thi (phiên chưa kết thúc, bài còn trong giờ): [ProctorSession,
     * Assessment], mới vào trước.
     */
    @Query("""
            select s, a from ProctorSession s join Assessment a on a.id = s.assessmentId
            where s.userId = :userId and s.status <> 2 and a.proctored = true and a.type = 2
              and (a.opensAt is null or a.opensAt <= :now) and (a.closesAt is null or a.closesAt >= :now)
            order by s.startedAt desc
            """)
    List<Object[]> findInExam(@Param("userId") long userId, @Param("now") Instant now);

    @Modifying
    @Query("update ProctorSession s set s.lastSeenAt = :now where s.id = :id")
    int touch(@Param("id") long id, @Param("now") Instant now);
}
