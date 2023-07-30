package nerds.studiousTestProject.studycafe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindRecommendStudycafeResponse {
    private String cafeName;
    private String photo;
    private Integer accumRevCnt;
    private Integer distance;
    private Double grade;
    private String[] hashtags;
}
