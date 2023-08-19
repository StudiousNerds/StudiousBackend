package nerds.studiousTestProject.review.dto.modify.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class ModifyReviewResponse {
    private Long reviewId;
    private LocalDate modifiedAt;
}
