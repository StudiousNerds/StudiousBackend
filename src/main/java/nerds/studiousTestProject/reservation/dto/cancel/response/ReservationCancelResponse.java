package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ReservationCancelResponse {

    private ReservationRecordInfoWithPlace reservation;
    private PaymentInfoWithRefund paymentRecord;
    private RefundPolicyInfoWithOnDay refundPolicy;

}
