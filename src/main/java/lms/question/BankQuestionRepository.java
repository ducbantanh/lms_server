package lms.question;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BankQuestionRepository extends JpaRepository<BankQuestion, Long> {

    /** Theo thứ tự thêm vào (giữ đúng thứ tự khi nhập từ văn bản). */
    List<BankQuestion> findByBankIdOrderById(long bankId);

    Optional<BankQuestion> findByPublicIdAndBankId(String publicId, long bankId);

    List<BankQuestion> findByBankIdAndPublicIdIn(long bankId, Collection<String> publicIds);

    long countByBankId(long bankId);

    /** Câu hỏi theo public id, chỉ trong các ngân hàng của ownerId (tạo bài thi từ ngân hàng của mình). */
    @Query("select q from BankQuestion q where q.publicId in :ids"
            + " and q.bankId in (select b.id from QuestionBank b where b.ownerId = :ownerId)")
    List<BankQuestion> findOwnedByPublicIds(@Param("ids") Collection<String> publicIds,
            @Param("ownerId") long ownerId);

    /** Số câu hỏi theo từng ngân hàng: [bankId, count]. */
    @Query("select q.bankId, count(q) from BankQuestion q where q.bankId in :bankIds group by q.bankId")
    List<Object[]> countByBankIds(@Param("bankIds") Collection<Long> bankIds);
}
