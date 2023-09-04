package nerds.studiousTestProject.review.dto.written.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.review.dto.find.response.PageInfo;

import java.util.List;

@Builder
@Data
public class WrittenReviewResponse {
    private PageInfo pageInfo;
    private List<WrittenReviewInfo> writtenReviewInfos;
}
