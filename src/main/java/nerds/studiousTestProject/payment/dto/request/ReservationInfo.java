package nerds.studiousTestProject.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfo {

    @NotNull(message = "예약날짜는 필수입니다.")
    private LocalDate reserveDate;
    @NotNull(message = "예약 시작 시간은 필수입니다.")
    private LocalTime startTime;
    @NotNull(message = "예약 종료 시간은 필수입니다.")
    private LocalTime endTime;
    @NotNull(message = "예약 이용 시간은 필수입니다.")
    private int duration;
    @NotNull(message = "예약 인원수는 필수입니다.")
    private int headCount;
    @NotNull(message = "예약 가격은 필수입니다.")
    private int price;

}
