package lms.code;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Không nạp entity CodeTest để đọc (nội dung test có thể rất lớn): dùng các truy vấn dưới đây. */
public interface CodeTestRepository extends JpaRepository<CodeTest, Long> {

    /** Test của một câu theo thứ tự; nội dung đầy đủ nếu ≤ 8192 byte, còn lại 300 ký tự đầu (CodeTestInfo). */
    @Query("""
            select new lms.code.CodeTestInfo(t.id, t.publicId, t.problemId, t.name, t.position, t.sample,
                t.inputSize, t.expectedSize,
                case when t.inputSize <= 8192 then t.input else substring(t.input, 1, 300) end,
                case when t.expectedSize <= 8192 then t.expected else substring(t.expected, 1, 300) end)
            from CodeTest t where t.problemId in :problemIds order by t.problemId, t.position
            """)
    List<CodeTestInfo> findInfos(@Param("problemIds") Collection<Long> problemIds);

    default List<CodeTestInfo> findInfos(long problemId) {
        return findInfos(List.of(problemId));
    }

    /** Nội dung đầy đủ một test: [input, expected]; rỗng nếu test đã bị xóa. */
    @Query("select t.input, t.expected from CodeTest t where t.id = :id")
    List<Object[]> content(@Param("id") long id);

    @Modifying
    @Query("update CodeTest t set t.position = :position, t.sample = :sample where t.id = :id")
    void updateMeta(@Param("id") long id, @Param("position") int position, @Param("sample") boolean sample);

    @Modifying
    @Query("delete from CodeTest t where t.id in :ids")
    void deleteByIds(@Param("ids") Collection<Long> ids);

    /** Số test theo từng câu: [problemId, count]. */
    @Query("select t.problemId, count(t) from CodeTest t where t.problemId in :ids group by t.problemId")
    List<Object[]> countByProblemIds(@Param("ids") Collection<Long> problemIds);
}
