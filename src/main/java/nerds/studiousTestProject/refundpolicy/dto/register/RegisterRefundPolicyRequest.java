package nerds.studiousTestProject.refundpolicy.dto.register;

import lombok.Data;
import nerds.studiousTestProject.refundpolicy.entity.Remaining;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import org.hibernate.validator.constraints.Range;

@Data
public class RegisterRefundPolicyRequest {
    @Range(min = 0, max = 8, message = "날짜는 최소 0에서 최대 8 사이 수여야 합니다.")
    private Integer day;    // 이용까지 남은 날짜

    @Range(min = 0, max = 100, message = "환불 비율는 최소 0에서 최대 100 사이 수여야 합니다.")
    private Integer rate;   // 환불 비율

    public RefundPolicy toEntity() {
        return RefundPolicy.builder()
                .remaining(Remaining.of(day))
                .rate(rate)
                .build();
    }
}
