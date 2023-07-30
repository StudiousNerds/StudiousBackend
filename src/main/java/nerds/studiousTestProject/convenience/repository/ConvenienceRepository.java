package nerds.studiousTestProject.convenience.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.convenience.entity.Convenience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConvenienceRepository extends JpaRepository<Convenience, Long> {
    @Query(value = "select c.name " +
            "from Convenience c " +
            "where c.studycafe = :studycafeId " +
            "and c.room is null", nativeQuery = true)
    List<String> findByStudycafeId(@Param("studycafeId") Long studycafeId);

    @Query(value = "select c.name " +
            "from Convenience c " +
            "where c.studycafe = :studycafeId " +
            "and c.room = :roomId", nativeQuery = true)
    List<String> findByStudycafeIdAndRoomId(@Param("studycafeId")Long studycafeId, @Param("roomId")Long roomId);
}
