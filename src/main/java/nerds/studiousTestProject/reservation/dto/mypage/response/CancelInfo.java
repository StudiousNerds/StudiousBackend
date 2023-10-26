package nerds.studiousTestProject.reservation.dto.mypage.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.payment.entity.Payment;

@Getter
@Builder
public class CancelInfo {

    private String reason;
    private MemberRole canceler;

    public static CancelInfo from(Payment payment) {
        return CancelInfo.builder()
                .reason(payment.getCancelReason())
                .canceler(payment.getCanceler())
                .build();
    }
}
