package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationInfoRepository extends JpaRepository<OperationInfo, Long> {
    @Query("select o from OperationInfo o " +
            "where o.studycafe.id =:studycafeId")
    List<OperationInfo> findAllByStudycafeId(Long studycafeId);
}
