package nerds.studiousTestProject.studycafe.dto.register.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.NearestStationInfo;

@Builder
@Data
public class NearestStationResponse {
    private Integer walkingTime;   // 분단위
    private String nearestStation;

    public NearestStationInfo toEmbedded() {
        return NearestStationInfo.builder()
                .walkingTime(walkingTime)
                .nearestStation(nearestStation)
                .build();
    }
}
