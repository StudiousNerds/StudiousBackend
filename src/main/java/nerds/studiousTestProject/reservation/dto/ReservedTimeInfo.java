package nerds.studiousTestProject.reservation.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

import java.time.LocalTime;

@Builder
@Data
@ToString
public class ReservedTimeInfo {
    private final LocalTime startTime;
    private final LocalTime endTime;

    public static ReservedTimeInfo of(ReservationRecord reservationRecord) {
        return ReservedTimeInfo.builder()
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .build();
    }
}
