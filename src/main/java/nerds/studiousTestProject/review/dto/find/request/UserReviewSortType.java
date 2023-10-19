package nerds.studiousTestProject.review.dto.find.request;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum UserReviewSortType {
    CREATED_DESC(Sort.by(Sort.Direction.DESC, "createdDate"));

    private final Sort sort;

    UserReviewSortType(Sort sort) {
        this.sort = sort;
    }

    public static class Names {
        public static final String CREATED_DATE_DESC = "CREATED_DESC";
    }
}
