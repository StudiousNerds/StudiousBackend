package nerds.studiousTestProject.review.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeleteReviewRequest {
    private Long studycafeId;
    private String[] hashtags;
}
