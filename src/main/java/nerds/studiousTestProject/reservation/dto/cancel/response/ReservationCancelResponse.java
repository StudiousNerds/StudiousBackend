package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class ReservationCancelResponse {

    private ReservationInfo reservation;
    private PlaceInfo place;
    private PaymentInfoWithRefund payment;
    private RefundPolicyInfoWithOnDay refundPolicy;

    public static ReservationCancelResponse of(ReservationRecord reservationRecord, PaymentInfoWithRefund paymentInfoWithRefund, List<RefundPolicy> refundPolicies, RefundPolicy refundPolicyOnDay) {
        return ReservationCancelResponse.builder()
                .reservation(ReservationInfo.from(reservationRecord))
                .place(PlaceInfo.from(reservationRecord))
                .payment(paymentInfoWithRefund)
                .refundPolicy(RefundPolicyInfoWithOnDay.of(refundPolicies, refundPolicyOnDay))
                .build();

    }
}
