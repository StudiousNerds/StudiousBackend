package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.Optional;

public interface OperationInfoRepository extends JpaRepository<OperationInfo, Long> {
    @Query("select o.startTime from OperationInfo as o " +
            "where o.week = :week")
    Optional<LocalTime> findStartTime(Week week);

    @Query("select o.endTime from OperationInfo as o " +
            "where o.week = :week")
    Optional<LocalTime> findEndTime(Week week);
}
