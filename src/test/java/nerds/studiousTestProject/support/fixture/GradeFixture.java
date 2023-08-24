package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Grade.GradeBuilder;
import nerds.studiousTestProject.review.entity.Review;

public enum GradeFixture {

    FIRST_GRADE(5, 5, 5, true, 5.0),
    SECOND_GRADE(3, 3, 3, true, 3.0),
    THIRD_GRADE(1, 1, 1, false, 1.0);


    private final Integer cleanliness;
    private final Integer deafening;
    private final Integer fixturesStatus;
    private final Boolean isRecommended;
    private final Double total;

    GradeFixture(Integer cleanliness, Integer deafening, Integer fixturesStatus, Boolean isRecommended, Double total) {
        this.cleanliness = cleanliness;
        this.deafening = deafening;
        this.fixturesStatus = fixturesStatus;
        this.isRecommended = isRecommended;
        this.total = total;
    }

    public Grade 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public GradeBuilder 기본_정보_생성() {
        return Grade.builder()
                .cleanliness(this.cleanliness)
                .deafening(this.deafening)
                .fixturesStatus(this.fixturesStatus)
                .isRecommended(this.isRecommended)
                .total(this.total);
    }

    public Grade 리뷰_생성(Review review, Long id) {
        return 기본_정보_생성()
                .id(id)
                .review(review)
                .build();
    }
}
