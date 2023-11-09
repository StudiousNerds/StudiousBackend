package nerds.studiousTestProject.studycafe.dto.enquiry.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;

import java.util.List;

@Builder
@Data
public class MainPageResponse {
    private List<SearchResponse>  recommend;
    private List<SearchResponse>  event;
}
