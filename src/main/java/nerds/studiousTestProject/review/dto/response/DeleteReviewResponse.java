package nerds.studiousTestProject.review.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class DeleteReviewResponse {
    private Long reviewId;
    private LocalDate deletedAt;
}
