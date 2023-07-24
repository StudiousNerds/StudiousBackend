package nerds.studiousTestProject.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfo {

    @NotBlank
    private LocalDate reserveDate;
    @NotBlank
    private LocalTime startTime;
    @NotBlank
    private LocalTime endTime;
    @NotBlank
    private int duration;
    @NotBlank
    private int headCount;
    @NotBlank
    private int price;

}
