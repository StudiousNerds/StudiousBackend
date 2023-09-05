package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FindReviewSortedResponse {
    private PageInfo pageInfo;
    private TotalGradeInfo totalGradeInfo;
    private List<FindReviewInfo> findReviewInfo;
}
