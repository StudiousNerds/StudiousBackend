package nerds.studiousTestProject.review.dto.enquiry.request;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum AdminReviewSortType {
    CREATED_DESC(Sort.by(Sort.Direction.DESC, "createdDate")),
    CREATED_ASC(Sort.by(Sort.Direction.ASC, "createdDate"));

    private final Sort sort;

    AdminReviewSortType(Sort sort) {
        this.sort = sort;
    }

    public static class Names {
        public static final String CREATED_DATE_DESC = "CREATED_DESC";
    }
}
