package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.domain.Page;

@Builder
@Data
public class PageInfo {
    private Integer totalPage;
    private Integer currentPage;

    public static PageInfo of(Page<Review> reviews) {
        return PageInfo.builder()
                .currentPage(reviews.getNumber() + 1)
                .totalPage(reviews.getTotalPages())
                .build();
    }
}
