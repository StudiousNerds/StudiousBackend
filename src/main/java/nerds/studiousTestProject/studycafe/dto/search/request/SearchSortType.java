package nerds.studiousTestProject.studycafe.dto.search.request;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum SearchSortType {
    GRADE_DESC(Sort.by(Sort.Direction.DESC, "grade")),
    RESERVATION_DESC(Sort.by(Sort.Direction.DESC, "reservationRecord")),
    REVIEW_DESC(Sort.by(Sort.Direction.DESC, "review")),
    REVIEW_ASC(Sort.by(Sort.Direction.ASC, "review")),
    CREATED_DESC(Sort.by(Sort.Direction.DESC, "createdDate"));

    private final Sort sort;

    SearchSortType(Sort sort) {
        this.sort = sort;
    }

    public static class Names {
        public static final String GRADE_DESC = "GRADE_DESC";
    }
}
