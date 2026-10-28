package lms.schedule;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByPublicId(String publicId);

    List<Room> findAllByOrderByNameAsc();

    /** Theo tên (collation của cột: không phân biệt hoa thường, dấu). */
    Optional<Room> findByName(String name);

    /** Trùng tên phòng (không phân biệt hoa thường theo collation của bảng). */
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
