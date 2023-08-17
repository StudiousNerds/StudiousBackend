package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.entity.SubPhoto.SubPhotoBuilder;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

public enum PhotoFixture {
    FIRST_PHOTO("www.spring.com"),
    SECOND_PHOTO("www.jpa.com");

    private final String url;

    PhotoFixture(String url) {
        this.url = url;
    }

    public SubPhotoBuilder 기본_정보_생성() {
        return SubPhoto.builder()
                .url(this.url);
    }

    public SubPhoto 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public SubPhoto 스터디카페_생성(Studycafe studycafe, Long id) {
        return 기본_정보_생성()
                .id(id)
                .studycafe(studycafe)
                .build();
    }

    public SubPhoto 룸_생성(Room room, Long id) {
        return 기본_정보_생성()
                .id(id)
                .room(room)
                .build();
    }

    public SubPhoto 리뷰_생성(Review review, Long id) {
        return 기본_정보_생성()
                .id(id)
                .review(review)
                .build();
    }
}
