package lms.auth.session;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    /** Session còn hiệu lực + user còn active. Fetch luôn user để tránh query thêm. */
    @Query("""
            select s from SessionEntity s join fetch s.user u
            where s.tokenHash = :hash
              and s.revokedAt is null
              and s.expiresAt > :now
              and u.status = 1
            """)
    Optional<SessionEntity> findValid(@Param("hash") String tokenHash, @Param("now") Instant now);

    @Query("""
            select s.id from SessionEntity s join s.user u
            where s.id in :ids
              and s.revokedAt is null
              and s.expiresAt > :now
              and u.status = 1
            """)
    List<Long> findValidIds(@Param("ids") Collection<Long> ids, @Param("now") Instant now);

    @Modifying
    @Query("""
            update SessionEntity s set s.revokedAt = :now, s.revokeReason = :reason
            where s.id = :id and s.revokedAt is null
            """)
    int revoke(@Param("id") long id, @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("""
            update SessionEntity s set s.revokedAt = :now, s.revokeReason = :reason
            where s.user.id = :userId and s.revokedAt is null
            """)
    int revokeAllForUser(@Param("userId") long userId, @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("""
            update SessionEntity s set s.revokedAt = :now, s.revokeReason = :reason
            where s.user.id = :userId and s.id <> :keepId and s.revokedAt is null
            """)
    int revokeOthersForUser(@Param("userId") long userId, @Param("keepId") long keepId,
            @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("delete from SessionEntity s where s.expiresAt < :before or s.revokedAt < :before")
    int purgeOlderThan(@Param("before") Instant before);
}
