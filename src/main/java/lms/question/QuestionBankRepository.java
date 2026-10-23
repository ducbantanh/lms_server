package lms.question;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

    /** Ngân hàng sửa gần nhất trước. */
    List<QuestionBank> findByOwnerIdOrderByUpdatedAtDescIdDesc(long ownerId);

    Optional<QuestionBank> findByPublicIdAndOwnerId(String publicId, long ownerId);
}
