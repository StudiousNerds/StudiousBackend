package nerds.studiousTestProject.review.dto.register.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.review.entity.Grade;

import java.util.List;

@Builder
@Data
public class RegisterReviewRequest {
    private Long reservationId;
    @NotNull(message = "청결도 평점은 필수입니다.")
    @Max(value = 5, message = "최대 5점까지 가능합니다.")
    private Integer cleanliness;
    @NotNull(message = "방음 평점은 필수입니다.")
    @Max(value = 5, message = "최대 5점까지 가능합니다.")
    private Integer deafening;
    @NotNull(message = "비품 상태 평점은 필수입니다.")
    @Max(value = 5, message = "최대 5점까지 가능합니다.")
    private Integer fixtureStatus;
    @NotNull(message = "추천 여부는 필수입니다.")
    private Boolean isRecommend;
    @NotEmpty(message = "해시태그는 필수입니다.")
    private List<String> hashtags;
    @NotBlank(message = "리뷰는 필수입니다.")
    @Size(max = 200, message = "최대 200자까지 작성가능합니다.")
    private String detail;

    public static Grade toGrade(RegisterReviewRequest registerReviewRequest) {
        return Grade.builder()
                .deafening(registerReviewRequest.getDeafening())
                .cleanliness(registerReviewRequest.getCleanliness())
                .fixturesStatus(registerReviewRequest.getFixtureStatus())
                .build();
    }
}
