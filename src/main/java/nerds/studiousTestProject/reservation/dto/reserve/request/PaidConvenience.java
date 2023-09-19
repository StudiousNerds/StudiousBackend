package nerds.studiousTestProject.reservation.dto.reserve.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class PaidConvenience {

    @NotBlank
    private String convenienceName;

    @NotBlank
    private Integer price;

}
