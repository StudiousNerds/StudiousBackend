package nerds.studiousTestProject.reservation.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.dto.PaymentResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationResponse;
import nerds.studiousTestProject.reservation.dto.ReserveUserResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Builder
@Getter
public class ShowAdminCancelResponse {

    private PlaceInfo place;
    private ReservationResponse reservation;
    private ReserveUserResponse user;
    private PaymentResponse payment;

    public static ShowAdminCancelResponse from(ReservationRecord reservationRecord) {
        Payment payment = reservationRecord.getPayment();
        return ShowAdminCancelResponse.builder()
                .payment(PaymentResponse.from(payment))
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationResponse.from(reservationRecord))
                .user(ReserveUserResponse.from(reservationRecord))
                .build();
    }

}
