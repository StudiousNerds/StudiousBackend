package nerds.studiousTestProject.room.repository;

import nerds.studiousTestProject.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByStudycafeId(Long id);
}
