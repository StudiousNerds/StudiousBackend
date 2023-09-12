package nerds.studiousTestProject.review.dto.modify.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ModifyReviewRequest {
    private Long cafeId;
    private Integer cleanliness;
    private Integer deafening;
    private Integer fixtureStatus;
    private Boolean isRecommend;
    private List<String> hashtags;
    private String detail;
}
