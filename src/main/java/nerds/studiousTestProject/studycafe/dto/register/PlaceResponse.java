package nerds.studiousTestProject.studycafe.dto.register;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PlaceResponse {
    private Integer duration;   // 분단위
    private String  nearestStation;
}
