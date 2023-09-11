package nerds.studiousTestProject.convenience.repository;

import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConvenienceRepository extends JpaRepository<Convenience, Long> {
    List<Convenience> findAllByRoom(Room room);
    List<Convenience> findAllByRoomOrStudycafe(Room room, Studycafe studycafe);
    void deleteAllByRoomId(Long roomId);
}
