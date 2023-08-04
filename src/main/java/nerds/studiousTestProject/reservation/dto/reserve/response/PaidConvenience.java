package nerds.studiousTestProject.reservation.dto.reserve.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class PaidConvenience {

    private String convenienceName;
    private Integer price;

}
