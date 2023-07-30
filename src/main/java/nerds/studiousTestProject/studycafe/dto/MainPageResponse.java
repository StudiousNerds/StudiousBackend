package nerds.studiousTestProject.studycafe.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MainPageResponse {
    private List<RecommendCafeResponse>  recommend;
    private List<EventCafeResponse>  event;
}
