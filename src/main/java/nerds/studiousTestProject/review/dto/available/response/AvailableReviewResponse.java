package nerds.studiousTestProject.review.dto.available.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.review.dto.find.response.PageInfo;

import java.util.List;

@Builder
@Data
public class AvailableReviewResponse {
    private PageInfo pageInfo;
    private List<AvailableReviewInfo> availableReviewInfo;
}
