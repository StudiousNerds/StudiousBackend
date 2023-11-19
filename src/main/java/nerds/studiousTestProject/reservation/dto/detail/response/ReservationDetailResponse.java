package nerds.studiousTestProject.reservation.dto.detail.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.dto.PaymentResponse;
import nerds.studiousTestProject.studycafe.dto.PlaceResponse;
import nerds.studiousTestProject.reservation.dto.ReservationResponse;
import nerds.studiousTestProject.reservation.dto.ReserveUserResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Getter
@Builder
public class ReservationDetailResponse {

    private PlaceResponse place;
    private ReservationResponse reservation;
    private PaymentResponse payment;
    private ReserveUserResponse user;

    public static ReservationDetailResponse of(ReservationRecord reservationRecord) {
        Payment payment = reservationRecord.getPayment();
        return ReservationDetailResponse.builder()
                .place(PlaceResponse.from(reservationRecord))
                .reservation(ReservationResponse.from(reservationRecord))
                .payment(PaymentResponse.from(payment))
                .user(ReserveUserResponse.from(reservationRecord))
                .build();
    }
}
