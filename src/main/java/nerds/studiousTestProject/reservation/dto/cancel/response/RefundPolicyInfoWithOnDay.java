package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyInfo;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class RefundPolicyInfoWithOnDay {

    private List<RefundPolicyInfo> refundPolicies;
    private RefundPolicyInfo onDay;

    public static RefundPolicyInfoWithOnDay of(List<RefundPolicy> refundPolicies, RefundPolicy refundPolicyOnDay) {
        return RefundPolicyInfoWithOnDay.builder()
                .refundPolicies(refundPolicies.stream().map(RefundPolicyInfo::from).collect(Collectors.toList()))
                .onDay(RefundPolicyInfo.from(refundPolicyOnDay))
                .build();
    }
}
