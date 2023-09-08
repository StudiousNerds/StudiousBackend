package nerds.studiousTestProject.reservation.dto.detail.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Getter
@Builder
public class ReservationDetailResponse {

    private PlaceInfo placeInfo;
    private PaymentInfo paymentInfo;
    private ReserveInfo reserveInfo;

    public static ReservationDetailResponse of(ReservationRecord reservationRecord, Studycafe studycafe, Room room, Payment payment) {
        return ReservationDetailResponse.builder()
                .placeInfo(PlaceInfo.of(studycafe, room, reservationRecord))
                .paymentInfo(PaymentInfo.from(payment))
                .reserveInfo(ReserveInfo.from(reservationRecord))
                .build();
    }
}
