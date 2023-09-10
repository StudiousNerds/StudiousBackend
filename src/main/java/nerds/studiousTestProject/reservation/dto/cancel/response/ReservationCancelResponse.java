package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ReservationCancelResponse {
    private ReservationRecordInfo reservationInfo;
    private PaymentInfo paymentInfo;
    private RefundPolicyInfoWithOnDay refundPolicyInfo;
}
