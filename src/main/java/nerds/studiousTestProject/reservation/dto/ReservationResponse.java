package nerds.studiousTestProject.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Builder
@Getter
public class ReservationResponse {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int usingTime;

    public static ReservationResponse from(ReservationRecord reservationRecord) {
        return ReservationResponse.builder()
                .date(reservationRecord.getDate())
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .usingTime(reservationRecord.getUsingTime())
                .build();

    }

}
