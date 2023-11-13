package nerds.studiousTestProject.reservation.dto.mypage.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.payment.entity.Payment;

@Getter
@Builder
public class CancelResponse {

    private String reason;
    private MemberRole canceler;

    public static CancelResponse from(Payment payment) {
        return CancelResponse.builder()
                .reason(payment.getCancelReason())
                .canceler(payment.getCanceler())
                .build();
    }
}
