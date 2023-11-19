package nerds.studiousTestProject.refundpolicy.repository;

import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.refundpolicy.entity.Remaining;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {
    List<RefundPolicy> findAllByStudycafe(Studycafe studycafe);

    @Query("select r.rate from RefundPolicy r where r.type = 'STUDYCAFE' and r.studycafe = :studycafe and r.remaining = :remaining")
    int findStudycafeRefundRateOnDay(@Param("studycafe") Studycafe studycafe, @Param("remaining") Remaining remaining);
}
