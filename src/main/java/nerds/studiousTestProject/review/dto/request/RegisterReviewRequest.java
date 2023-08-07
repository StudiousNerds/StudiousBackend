package nerds.studiousTestProject.review.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterReviewRequest {
    private Long cafeId;
    private Long reservationId;
    private Integer cleanliness;
    private Integer deafening;
    private Integer fixtureStatus;
    private Boolean isRecommend;
    private String[] hashtags;
    private String[] photos;
    private String detail;
}
