package nerds.studiousTestProject.review.dto.register.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class RegisterReviewResponse {
    private Long reviewId;
    private LocalDate createdAt;
}
