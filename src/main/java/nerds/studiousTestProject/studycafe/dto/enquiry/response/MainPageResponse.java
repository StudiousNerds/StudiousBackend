package nerds.studiousTestProject.studycafe.dto.enquiry.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MainPageResponse {
    private List<RecommendCafeResponse>  recommend;
    private List<EventCafeResponse>  event;
}
