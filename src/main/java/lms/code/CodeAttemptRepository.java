package lms.code;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CodeAttemptRepository extends JpaRepository<CodeAttempt, Long> {

    String INFO = "select new lms.code.CodeAttemptInfo(a.id, a.publicId, a.problemId, a.userId, a.kind,"
            + " a.language, a.status, a.verdict, a.passed, a.total, a.createdAt, a.judgedAt) from CodeAttempt a ";

    /** Mọi lần chạy/nộp của một sinh viên trong bài, mới nhất trước. */
    @Query(INFO + "where a.assessmentId = :assessmentId and a.userId = :userId order by a.id desc")
    List<CodeAttemptInfo> infosOfUser(@Param("assessmentId") long assessmentId, @Param("userId") long userId);

    /** Của một câu, mới nhất trước. */
    @Query(INFO + "where a.problemId = :problemId and a.userId = :userId order by a.id desc")
    List<CodeAttemptInfo> infosOfProblem(@Param("problemId") long problemId, @Param("userId") long userId);

    /** Lần nộp bài của cả bài (bảng kết quả). */
    @Query(INFO + "where a.assessmentId = :assessmentId and a.kind = 1")
    List<CodeAttemptInfo> submitInfos(@Param("assessmentId") long assessmentId);

    /** Xóa hàng loạt; kết quả từng test xóa theo (ON DELETE CASCADE). */
    @Modifying
    @Query("delete from CodeAttempt a where a.id in :ids")
    void deleteByIds(@Param("ids") Collection<Long> ids);

    Optional<CodeAttempt> findByPublicIdAndAssessmentId(String publicId, long assessmentId);

    Optional<CodeAttempt> findByPublicIdAndAssessmentIdAndUserId(String publicId, long assessmentId, long userId);

    List<CodeAttempt> findByAssessmentIdAndKind(long assessmentId, int kind);

    List<CodeAttempt> findByProblemId(long problemId);

    /** Lần nộp bài được tính (lỗi máy chấm không tính); dùng cho giới hạn số lần nộp. */
    @Query("select count(a) from CodeAttempt a where a.problemId = :problemId and a.userId = :userId"
            + " and a.kind = 1 and (a.status <> 2 or a.verdict <> 'SE')")
    long countCountedSubmits(@Param("problemId") long problemId, @Param("userId") long userId);

    /** Còn bài chờ/đang chấm ở câu này: mỗi sinh viên một lần chờ chấm mỗi câu. */
    @Query("select count(a) > 0 from CodeAttempt a where a.problemId = :problemId and a.userId = :userId"
            + " and a.status <> 2")
    boolean hasPending(@Param("problemId") long problemId, @Param("userId") long userId);

    /** Bài chờ chấm, cũ trước; bài tự chấm lại vì lỗi máy chấm chỉ nhận khi tới retryAt. */
    @Query("""
            select a.id from CodeAttempt a
            where a.status = 0 and (a.retryAt is null or a.retryAt <= :now)
            order by a.id
            """)
    List<Long> findQueuedIds(@Param("now") Instant now, Limit limit);

    /** Khóa dòng khi worker nhận bài hoặc lưu kết quả: chấm lại (sửa test) không chen vào giữa. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from CodeAttempt a where a.id = :id")
    Optional<CodeAttempt> lockById(@Param("id") long id);

    /** Server dừng giữa lúc chấm: đưa bài đang chấm về hàng đợi (CodeJudgeWorker gọi khi khởi động). */
    @Modifying
    @Query("update CodeAttempt a set a.status = 0, a.claimToken = null where a.status = 1")
    int requeueRunning();

    /** Số bài chờ/đang chấm của một bài (giảng viên xem tiến độ chấm lại). */
    @Query("select count(a) from CodeAttempt a where a.assessmentId = :assessmentId and a.status <> 2")
    long countPending(@Param("assessmentId") long assessmentId);
}
