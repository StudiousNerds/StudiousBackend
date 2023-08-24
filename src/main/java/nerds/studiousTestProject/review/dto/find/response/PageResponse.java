package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.domain.Page;

@Builder
@Data
public class PageResponse {
    private Integer totalPage;
    private Integer currentPage;

    public static PageResponse of(Page<Review> reviews) {
        return PageResponse.builder()
                .currentPage(reviews.getNumber())
                .totalPage(reviews.getTotalPages())
                .build();
    }
}
