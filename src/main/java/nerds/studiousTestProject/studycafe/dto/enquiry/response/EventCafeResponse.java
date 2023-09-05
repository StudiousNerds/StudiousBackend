package nerds.studiousTestProject.studycafe.dto.enquiry.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class EventCafeResponse {
    private Long studycafeId;
    private String studycafeName;
    private String photo;
    private Integer accumRevCnt;
    private String nearestStation;
    private Integer walkingTime;
    private Double grade;
    private List<String> hashtags;
}