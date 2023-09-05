package nerds.studiousTestProject.review.dto.available.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AvailableReviewResponse {
    private Integer totalPage;
    private Integer currentPage;
    private List<AvailableReviewInfo> availableReviewInfo;
}