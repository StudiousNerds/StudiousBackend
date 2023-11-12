package nerds.studiousTestProject.reservation.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

import java.time.LocalTime;

@Builder
@Data
@ToString
public class ReservedTimeResponse {
    private final LocalTime startTime;
    private final LocalTime endTime;

    public static ReservedTimeResponse of(ReservationRecord reservationRecord) {
        return ReservedTimeResponse.builder()
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .build();
    }
}
