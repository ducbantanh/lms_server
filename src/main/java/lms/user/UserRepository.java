package lms.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lms.auth.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    /** Collation utf8mb4_unicode_ci => so khớp không phân biệt hoa thường. */
    Optional<User> findByUsername(String username);

    Optional<User> findByPublicId(String publicId);

    List<User> findByPublicIdIn(Collection<String> publicIds);

    boolean existsByRole(Role role);

    long countByRoleAndStatus(Role role, int status);

    @Query("select u.username from User u where u.username in :usernames")
    List<String> findExistingUsernames(@Param("usernames") Collection<String> usernames);

    @Query("""
            select u from User u
            where (:role is null or u.role = :role)
              and (:status is null or u.status = :status)
              and (:q is null
                   or lower(u.username) like lower(concat('%', :q, '%'))
                   or lower(u.fullName) like lower(concat('%', :q, '%')))
            """)
    Page<User> search(@Param("q") String q, @Param("role") Role role, @Param("status") Integer status,
            Pageable pageable);
}
