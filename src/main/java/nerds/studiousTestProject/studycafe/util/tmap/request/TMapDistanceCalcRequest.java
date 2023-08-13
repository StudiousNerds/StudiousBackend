package nerds.studiousTestProject.studycafe.util.tmap.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TMapDistanceCalcRequest {
    String startX;
    String startY;
    String endX;
    String endY;
    String startName;
    String endName;
}
