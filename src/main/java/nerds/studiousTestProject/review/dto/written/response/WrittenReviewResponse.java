package nerds.studiousTestProject.review.dto.written.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class WrittenReviewResponse {
    private Long reservationId;
    private StudycafeInfo studycafeInfo;
    private GradeInfo gradeInfo;
    private ReviewInfo reviewInfo;
}
