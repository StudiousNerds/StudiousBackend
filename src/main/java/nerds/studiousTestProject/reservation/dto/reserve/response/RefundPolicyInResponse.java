package nerds.studiousTestProject.reservation.dto.reserve.response;

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
                .day(refundPolicy.getRefundDay().getMessage())
                .rate(refundPolicy.getRate())
                .build();
    }

}
