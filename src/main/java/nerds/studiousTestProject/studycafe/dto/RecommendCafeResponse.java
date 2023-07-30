package nerds.studiousTestProject.studycafe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendCafeResponse {
    private Long cafeId;
    private String cafeName;
    private String photo;
    private Integer accumRevCnt;
    private String nearestStation;
    private Integer distance;
    private Double grade;
    private String[] hashtags;
}