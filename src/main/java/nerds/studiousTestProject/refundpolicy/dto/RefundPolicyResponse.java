package nerds.studiousTestProject.refundpolicy.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;

@Builder
@Getter
public class RefundPolicyResponse {
    private String day;
    private Integer rate;

    public static RefundPolicyResponse from(RefundPolicy refundPolicy) {
        return RefundPolicyResponse.builder()
                .day(refundPolicy.getRemaining().getMessage())
                .rate(refundPolicy.getRate())
                .build();
    }

}
