package nerds.studiousTestProject.refundpolicy.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;

@Builder
@Getter
public class RefundPolicyInfo {
    private String day;
    private Integer rate;

    public static RefundPolicyInfo from(RefundPolicy refundPolicy) {
        return RefundPolicyInfo.builder()
                .day(refundPolicy.getRemaining().getMessage())
                .rate(refundPolicy.getRate())
                .build();
    }

}
