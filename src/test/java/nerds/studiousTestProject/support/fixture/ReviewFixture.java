package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.entity.Review.ReviewBuilder;

import java.time.LocalDate;

public enum ReviewFixture {

    FIRST_REVIEW(LocalDate.now(), "나는 여기 좋아요"),
    SECOND_REVIEW(LocalDate.now().minusDays(1), "여긴 좀 별로.."),
    THIRD_REVIEW(LocalDate.now().minusDays(5), "최악임...");

    private final String detail;
    private final LocalDate createdDate;

    ReviewFixture(LocalDate createdDate, String detail) {
        this.createdDate = createdDate;
        this.detail = detail;
    }

    public Review 생성() {
        return 생성(null);
    }

    public Review 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public Review 평점_생성(Grade grade) {
        return 평점_생성(grade, null);
    }

    public Review 평점_생성(Grade grade, Long id) {
        return 기본_정보_생성()
                .id(id)
                .grade(grade)
                .build();
    }

    public ReviewBuilder 기본_정보_생성() {
        return Review.builder()
                .createdDate(this.createdDate)
                .detail(this.detail);
    }
}
