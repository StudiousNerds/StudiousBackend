package nerds.studiousTestProject.reservation.dto.reserve.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ReservationInfo {

    @NotNull(message = "예약날짜는 필수입니다.")
    private LocalDate date;
    @NotNull(message = "예약 시작 시간은 필수입니다.")
    private LocalTime startTime;
    @NotNull(message = "예약 종료 시간은 필수입니다.")
    private LocalTime endTime;
    @NotNull(message = "예약 이용 시간은 필수입니다.")
    private Integer usingTime;
    @NotNull(message = "예약 인원수는 필수입니다.")
    private Integer headCount;

}
