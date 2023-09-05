package nerds.studiousTestProject.review.dto.written.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class WrittenReviewResponse {
    private Integer totalPage;
    private Integer currentPage;
    private List<WrittenReviewInfo> writtenReviewInfos;
}
