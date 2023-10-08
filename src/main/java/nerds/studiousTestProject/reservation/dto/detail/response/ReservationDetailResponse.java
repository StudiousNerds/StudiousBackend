package nerds.studiousTestProject.reservation.dto.detail.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.dto.PaymentInfo;
import nerds.studiousTestProject.reservation.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Getter
@Builder
public class ReservationDetailResponse {

    private PlaceInfo place;
    private ReservationInfo reservation;
    private PaymentInfo payment;
    private ReserveUserInfo reserveUser;

    public static ReservationDetailResponse of(ReservationRecord reservationRecord, Payment payment) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        return ReservationDetailResponse.builder()
                .place(PlaceInfo.of(studycafe, room))
                .reservation(ReservationInfo.from(reservationRecord))
                .payment(PaymentInfo.from(payment))
                .reserveUser(ReserveUserInfo.from(reservationRecord))
                .build();
    }
}
