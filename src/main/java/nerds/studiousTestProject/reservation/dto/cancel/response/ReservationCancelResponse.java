package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.ReservationResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.studycafe.dto.PlaceResponse;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class ReservationCancelResponse {

    private ReservationResponse reservation;
    private PlaceResponse place;
    private PaymentWithRefundResponse paymentWithRefund;
    private RefundPolicyWithOnDayResponse refundPolicy;

    public static ReservationCancelResponse of(ReservationRecord reservationRecord, PaymentWithRefundResponse paymentWithRefundResponse, List<RefundPolicy> refundPolicies, RefundPolicy refundPolicyOnDay) {
        return ReservationCancelResponse.builder()
                .reservation(ReservationResponse.from(reservationRecord))
                .place(PlaceResponse.from(reservationRecord))
                .paymentWithRefund(paymentWithRefundResponse)
                .refundPolicy(RefundPolicyWithOnDayResponse.of(refundPolicies, refundPolicyOnDay))
                .build();

    }
}
