package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NearestStationInfo {
    @Column(name = "walking_time", nullable = true)
    private Integer walkingTime;

    @Column(name = "nearest_station", nullable = true)
    private String nearestStation;

    @Builder
    public NearestStationInfo(Integer walkingTime, String nearestStation) {
        this.walkingTime = walkingTime;
        this.nearestStation = nearestStation;
    }
}
