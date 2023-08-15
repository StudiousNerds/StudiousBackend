package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NearestStationInfo {
    private Integer walkingTime;
    private String nearestStation;

    @Builder
    public NearestStationInfo(Integer walkingTime, String nearestStation) {
        this.walkingTime = walkingTime;
        this.nearestStation = nearestStation;
    }
}
