package nerds.studiousTestProject.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;

@Builder
@Getter
public class RefundPolicyInResponse {
    private String day;
    private Integer rate;

    public static RefundPolicyInResponse from(RefundPolicy refundPolicy) {
        return RefundPolicyInResponse.builder()
                .day(refundPolicy.getRemaining().getMessage())
                .rate(refundPolicy.getRate())
                .build();
    }

}
