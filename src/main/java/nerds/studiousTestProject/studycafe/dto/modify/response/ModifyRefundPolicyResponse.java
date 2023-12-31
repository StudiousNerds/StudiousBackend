package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;

@Builder
@Data
public class ModifyRefundPolicyResponse {
    private Integer day;    // 이용까지 남은 날짜
    private Integer rate;   // 환불 비율

    public static ModifyRefundPolicyResponse from(RefundPolicy refundPolicy) {
        return ModifyRefundPolicyResponse.builder()
                .day(refundPolicy.getRemaining().getRemain())
                .rate(refundPolicy.getRate())
                .build();
    }
}
