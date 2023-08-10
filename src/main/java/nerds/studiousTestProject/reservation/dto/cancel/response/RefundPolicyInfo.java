package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInResponse;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class RefundPolicyInfo {

    private List<RefundPolicyInResponse> refundPolicy;
    private RefundPolicyInResponse refundPolicyOnDay;

    public static RefundPolicyInfo of(List<RefundPolicy> refundPolicies, RefundPolicy refundPolicyOnDay) {
        return RefundPolicyInfo.builder()
                .refundPolicy(refundPolicies.stream().map(RefundPolicyInResponse::from).collect(Collectors.toList()))
                .refundPolicyOnDay(RefundPolicyInResponse.from(refundPolicyOnDay))
                .build();
    }
}
