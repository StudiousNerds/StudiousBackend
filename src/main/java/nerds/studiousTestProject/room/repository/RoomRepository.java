package nerds.studiousTestProject.room.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByStudycafeId(Long id);

    @Query("select r from Room r join fetch r.studycafe where r.id = :roomId")
    Optional<Room> findByIdWithStudycafe(@Param("roomId") Long roomId);
}
