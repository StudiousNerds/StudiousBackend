package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OperationInfoRepository extends JpaRepository<OperationInfo, Long> {

    @Query("select o from OperationInfo o " +
            "where o.studycafe.id = :studycafeId and o.week = :week")
    Optional<OperationInfo> findByStudycafeAndWeek(final Long studycafeId, final Week week);
}
