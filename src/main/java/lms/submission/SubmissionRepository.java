package lms.submission;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    /**
     * "Đã nộp" giống SubmissionService.statusOf: bấm nộp, hoặc bài đã hết giờ mà có file (nộp khi hết giờ).
     * Bài được chấm điểm khi sinh viên không nộp (dòng không có file) không tính.
     */
    String FINISHED = """
            (s.submittedAt is not null or (a.closesAt < :now
                and exists (select f.id from SubmissionFile f where f.submissionId = s.id)))
            """;

    Optional<Submission> findByAssessmentIdAndUserId(long assessmentId, long userId);

    /**
     * Trong danh sách, những người đang thi: ghi danh khóa học có bài loại `type` đang mở và chưa bấm nộp.
     * RealtimeSweeper giữ phiên của họ để không bị tự đăng xuất khi không hoạt động.
     */
    @Query("""
            select distinct e.userId from Enrollment e, Assessment a
            where e.userId in :userIds and a.courseId = e.courseId
              and a.type = :type and a.opensAt <= :now and a.closesAt > :now
              and not exists (select s.id from Submission s
                              where s.assessmentId = a.id and s.userId = e.userId and s.submittedAt is not null)
            """)
    List<Long> findTakingAssessment(@Param("userIds") Collection<Long> userIds, @Param("type") int type,
            @Param("now") Instant now);

    List<Submission> findByAssessmentId(long assessmentId);

    /** Bài nộp của mọi bài trong danh sách (bảng điểm cả khóa học). */
    List<Submission> findByAssessmentIdIn(Collection<Long> assessmentIds);

    /** Bài nộp của một sinh viên trong các bài của một khóa học. */
    List<Submission> findByUserIdAndAssessmentIdIn(long userId, Collection<Long> assessmentIds);

    /** Số bài một loại đã nộp của một sinh viên theo từng khóa học: [courseId, count]. */
    @Query("select a.courseId, count(s) from Submission s, Assessment a"
            + " where s.assessmentId = a.id and s.userId = :userId and a.courseId in :courseIds and a.type = :type"
            + " and " + FINISHED + " group by a.courseId")
    List<Object[]> countFinishedByUser(@Param("userId") long userId, @Param("courseIds") Collection<Long> courseIds,
            @Param("type") int type, @Param("now") Instant now);

    /** Số bài một loại đã nộp theo từng sinh viên của một khóa học: [userId, count]. */
    @Query("select s.userId, count(s) from Submission s, Assessment a"
            + " where s.assessmentId = a.id and a.courseId = :courseId and a.type = :type"
            + " and " + FINISHED + " group by s.userId")
    List<Object[]> countFinishedByCourse(@Param("courseId") long courseId, @Param("type") int type,
            @Param("now") Instant now);

    /** Như trên cho nhiều khóa học: [courseId, userId, count]. */
    @Query("select a.courseId, s.userId, count(s) from Submission s, Assessment a"
            + " where s.assessmentId = a.id and a.courseId in :courseIds and a.type = :type"
            + " and " + FINISHED + " group by a.courseId, s.userId")
    List<Object[]> countFinishedByCourseAndUser(@Param("courseIds") Collection<Long> courseIds,
            @Param("type") int type, @Param("now") Instant now);
}
