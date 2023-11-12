package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyResponse;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class RefundPolicyWithOnDayResponse {

    private List<RefundPolicyResponse> refundPolicies;
    private RefundPolicyResponse onDay;

    public static RefundPolicyWithOnDayResponse of(List<RefundPolicy> refundPolicies, RefundPolicy refundPolicyOnDay) {
        return RefundPolicyWithOnDayResponse.builder()
                .refundPolicies(refundPolicies.stream().map(RefundPolicyResponse::from).collect(Collectors.toList()))
                .onDay(RefundPolicyResponse.from(refundPolicyOnDay))
                .build();
    }
}
