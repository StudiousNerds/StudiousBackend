package nerds.studiousTestProject.convenience.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConvenienceRepository extends JpaRepository<Convenience, Long> {
    List<Convenience> findAllByRoom(Room room);
    void deleteAllByRoomId(Long roomId);

    @Query("select c from Convenience  c where c.room.id = :roomId and c.isFree = false ")
    List<Convenience> findAllByRoomIdWherePaid(@Param("roomId") Long roomId);
}
