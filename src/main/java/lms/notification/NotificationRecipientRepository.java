package lms.notification;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRecipientRepository
        extends JpaRepository<NotificationRecipient, NotificationRecipient.Key> {

    /** Thông báo của một người, mới nhất trước: [Notification, NotificationRecipient]; category null = mọi loại. */
    @Query(value = """
            select n, r from Notification n join NotificationRecipient r on r.notificationId = n.id
            where r.userId = :userId
              and (:category is null or n.category = :category)
              and (:unreadOnly = false or r.readAt is null)
            order by n.createdAt desc, n.id desc
            """, countQuery = """
            select count(r) from Notification n join NotificationRecipient r on r.notificationId = n.id
            where r.userId = :userId
              and (:category is null or n.category = :category)
              and (:unreadOnly = false or r.readAt is null)
            """)
    Page<Object[]> findForUser(@Param("userId") long userId, @Param("category") Integer category,
            @Param("unreadOnly") boolean unreadOnly, Pageable page);

    /** Số chưa đọc theo loại: [category, count]. */
    @Query("""
            select n.category, count(r) from Notification n join NotificationRecipient r on r.notificationId = n.id
            where r.userId = :userId and r.readAt is null
            group by n.category
            """)
    List<Object[]> unreadByCategory(@Param("userId") long userId);

    /** Số người nhận và số đã đọc theo thông báo: [notificationId, recipients, read]. */
    @Query("""
            select r.notificationId, count(r), sum(case when r.readAt is null then 0 else 1 end)
            from NotificationRecipient r where r.notificationId in :ids group by r.notificationId
            """)
    List<Object[]> readStats(@Param("ids") Collection<Long> ids);

    @Modifying
    @Query("""
            update NotificationRecipient r set r.readAt = :now
            where r.userId = :userId and r.notificationId = :notificationId and r.readAt is null
            """)
    int markRead(@Param("userId") long userId, @Param("notificationId") long notificationId,
            @Param("now") Instant now);

    @Modifying
    @Query("update NotificationRecipient r set r.readAt = :now where r.userId = :userId and r.readAt is null")
    int markAllRead(@Param("userId") long userId, @Param("now") Instant now);

    boolean existsByNotificationIdAndUserId(long notificationId, long userId);
}
