package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PageResponse {
    private Integer totalPage;
    private Integer currentPage;
}
