package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.entity.Review.ReviewBuilder;

import java.time.LocalDate;

public enum ReviewFixture {

    FIRST_REVIEW(LocalDate.now(), "나는 여기 좋아요", true),
    SECOND_REVIEW(LocalDate.now().minusDays(1), "여긴 좀 별로..", true),
    THIRD_REVIEW(LocalDate.now().minusDays(5), "최악임...", true);

    private final String detail;
    private final LocalDate createdDate;
    private final boolean isRecommended;

    ReviewFixture(LocalDate createdDate, String detail, boolean isRecommended) {
        this.createdDate = createdDate;
        this.detail = detail;
        this.isRecommended = isRecommended;
    }

    public Review 생성() {
        return 생성(null);
    }

    public Review 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public Review 평점_정보_생성(Integer cleanliness, Integer deafening, Integer fixturesStatus, Double total) {
        return 평점_정보_생성(null, cleanliness, deafening, fixturesStatus, total);
    }

    public Review 평점_정보_생성(Long id, Integer cleanliness, Integer deafening, Integer fixturesStatus, Double total) {
        return 기본_정보_생성()
                .grade(createGrade(cleanliness, deafening, fixturesStatus, total))
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

    public Grade createGrade(Integer cleanliness, Integer deafening, Integer fixturesStatus, Double total) {
        return Grade.builder()
                .cleanliness(cleanliness)
                .deafening(deafening)
                .fixturesStatus(fixturesStatus)
                .total(total)
                .build();
    }

    public ReviewBuilder 기본_정보_생성() {
        return Review.builder()
                .createdDate(this.createdDate)
                .detail(this.detail)
                .isRecommended(this.isRecommended);
    }
}
