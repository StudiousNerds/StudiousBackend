package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FindReviewSortedResponse {
    private PageResponse pageResponse;
    private TotalGradeInfo totalGradeInfo;
    private List<FindReviewResponse> findReviewResponses;
}
