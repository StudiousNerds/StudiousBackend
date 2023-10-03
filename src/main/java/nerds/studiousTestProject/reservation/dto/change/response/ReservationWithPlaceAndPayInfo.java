package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payments;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationWithPlaceAndPayInfo {

    private String studycafePhoto;
    private String studycafeName;
    private String roomName;
    private String paymentMethod;
    private int price;
    private LocalDate reserveDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public static ReservationWithPlaceAndPayInfo of(Payments payments, ReservationRecord reservationRecord) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        return ReservationWithPlaceAndPayInfo.builder()
                .studycafeName(studycafe.getName())
                .studycafePhoto(studycafe.getPhoto())
                .roomName(room.getName())
                .paymentMethod(payments.getMethods())
                .price(payments.getTotalPrice())
                .reserveDate(reservationRecord.getDate())
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .build();
    }

}
