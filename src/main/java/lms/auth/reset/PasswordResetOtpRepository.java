package lms.auth.reset;

import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from PasswordResetOtp o where o.userId = :userId")
    Optional<PasswordResetOtp> findForUpdate(@Param("userId") long userId);

    @Modifying
    @Query("delete from PasswordResetOtp o where o.expiresAt < :before")
    int deleteExpiredBefore(@Param("before") Instant before);
}
