package nerds.studiousTestProject.reservation.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.dto.PaymentInfo;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.dto.ReserveUserInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Builder
@Getter
public class ShowAdminCancelResponse {

    private PlaceInfo place;
    private ReservationInfo reservation;
    private ReserveUserInfo user;
    private PaymentInfo payment;

    public static ShowAdminCancelResponse from(ReservationRecord reservationRecord) {
        Payment payment = reservationRecord.getPayment();
        return ShowAdminCancelResponse.builder()
                .payment(PaymentInfo.from(payment))
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationInfo.from(reservationRecord))
                .user(ReserveUserInfo.from(reservationRecord))
                .build();
    }

}
