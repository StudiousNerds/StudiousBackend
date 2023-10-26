package nerds.studiousTestProject.reservation.dto.detail.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.dto.PaymentInfo;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.dto.ReserveUserInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Getter
@Builder
public class ReservationDetailResponse {

    private PlaceInfo place;
    private ReservationInfo reservation;
    private PaymentInfo payment;
    private ReserveUserInfo user;

    public static ReservationDetailResponse of(ReservationRecord reservationRecord) {
        Payment payment = reservationRecord.getPayment();
        return ReservationDetailResponse.builder()
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationInfo.from(reservationRecord))
                .payment(PaymentInfo.from(payment))
                .user(ReserveUserInfo.from(reservationRecord))
                .build();
    }
}
