package nerds.studiousTestProject.payment.dto.confirm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInfo {

    private String studycafeName;
    private String roomName;
    private LocalDate reserveDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int usingTime;

    public static ReservationInfo from(ReservationRecord reservationRecord) {
        return ReservationInfo.builder()
                .reserveDate(reservationRecord.getDate())
                .roomName(reservationRecord.getRoom().getName())
                .studycafeName(reservationRecord.getRoom().getStudycafe().getName())
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .usingTime(reservationRecord.getDuration())
                .build();
    }

}
