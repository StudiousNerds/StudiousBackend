package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FindReviewSortedResponse {
    private Integer totalPage;
    private Integer currentPage;
    private TotalGradeInfo totalGradeInfo;
    private List<FindReviewInfo> findReviewInfo;
}
