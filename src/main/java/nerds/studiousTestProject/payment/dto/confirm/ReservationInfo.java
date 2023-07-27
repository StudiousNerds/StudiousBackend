package nerds.studiousTestProject.payment.dto.confirm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
