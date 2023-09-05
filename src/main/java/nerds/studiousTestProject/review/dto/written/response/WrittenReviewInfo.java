package nerds.studiousTestProject.review.dto.written.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WrittenReviewInfo {
    private Long reservationId;
    private StudycafeInfo studycafeInfo;
    private GradeInfo gradeInfo;
    private ReviewInfo reviewInfo;
}
