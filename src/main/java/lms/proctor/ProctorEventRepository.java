package lms.proctor;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProctorEventRepository extends JpaRepository<ProctorEvent, Long> {

    List<ProctorEvent> findBySessionIdOrderByIdAsc(long sessionId);

    long countBySessionId(long sessionId);

    /** Sự kiện gần nhất của mỗi phiên (bảng giám thị). */
    @Query("""
            select e from ProctorEvent e where e.id in (
              select max(x.id) from ProctorEvent x where x.sessionId in :ids group by x.sessionId)
            """)
    List<ProctorEvent> findLatest(@Param("ids") Collection<Long> sessionIds);
}
