package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord.HashtagRecordBuilder;
import nerds.studiousTestProject.review.entity.Review;

public enum HashtagFixture {
    COST_EFFECTIVE_HASHTAG(HashtagName.COST_EFFECTIVE),
    ACCESS_HASHTAG(HashtagName.ACCESS);

    private HashtagName name;

    HashtagFixture(HashtagName name) {
        this.name = name;
    }

    public HashtagRecord 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public HashtagRecordBuilder 기본_정보_생성() {
        return HashtagRecord.builder()
                .name(this.name);
    }

    public HashtagRecord 리뷰_생성(Review review, Long id) {
        return 기본_정보_생성()
                .id(id)
                .review(review)
                .build();
    }

    public HashtagRecord 리뷰_생성(Review review) {
        return 리뷰_생성(review, null);
    }
}
