package lms.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Audit mới nhất trước, kèm người thực hiện ([AuditLog, User]; User null nếu là hệ thống hoặc đã bị xóa).
     * actionPrefix null = mọi loại ("course." = mọi thao tác khóa học); q là chuỗi like đã viết thường, có %.
     */
    @Query(value = """
            select a, u from AuditLog a left join User u on u.id = a.actorUserId
            where (:actionPrefix is null or a.action like concat(:actionPrefix, '%'))
              and (:q is null or lower(a.target) like :q or lower(a.detail) like :q
                   or lower(u.username) like :q or lower(u.fullName) like :q)
            order by a.id desc
            """, countQuery = """
            select count(a) from AuditLog a left join User u on u.id = a.actorUserId
            where (:actionPrefix is null or a.action like concat(:actionPrefix, '%'))
              and (:q is null or lower(a.target) like :q or lower(a.detail) like :q
                   or lower(u.username) like :q or lower(u.fullName) like :q)
            """)
    Page<Object[]> search(@Param("actionPrefix") String actionPrefix, @Param("q") String q, Pageable pageable);
}
