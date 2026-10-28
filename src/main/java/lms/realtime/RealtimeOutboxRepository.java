package lms.realtime;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RealtimeOutboxRepository extends JpaRepository<RealtimeOutbox, Long> {

    /** Sự kiện ghi sau thời điểm cho trước, cũ trước (RealtimeOutboxPoller đọc theo khung thời gian). */
    List<RealtimeOutbox> findByCreatedAtAfterOrderByIdAsc(Instant since, Limit limit);

    /** Dọn sự kiện cũ: mọi server web đã đọc từ lâu (mỗi server tự nhớ đã gửi gì, không xóa khi đọc). */
    @Modifying
    @Query("delete from RealtimeOutbox o where o.createdAt < :before")
    int deleteOlderThan(@Param("before") Instant before);
}
