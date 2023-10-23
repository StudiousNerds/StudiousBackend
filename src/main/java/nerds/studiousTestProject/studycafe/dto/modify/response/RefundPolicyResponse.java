package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;

@Builder
@Data
public class RefundPolicyResponse {
    private Integer day;    // 이용까지 남은 날짜
    private Integer rate;   // 환불 비율

    public static RefundPolicyResponse from(RefundPolicy refundPolicy) {
        return RefundPolicyResponse.builder()
                .day(refundPolicy.getRemaining().getRemain())
                .rate(refundPolicy.getRate())
                .build();
    }
}
