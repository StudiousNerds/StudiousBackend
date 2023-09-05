package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.entity.SubPhoto.SubPhotoBuilder;
import nerds.studiousTestProject.photo.entity.SubPhotoType;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

public enum PhotoFixture {
    STUDYCAFE_PHOTO("www.spring.com", SubPhotoType.STUDYCAFE),
    ROOM_PHOTO("www.jpa.com", SubPhotoType.ROOM),
    REVIEW_PHOTO("www.naver.com", SubPhotoType.REVIEW);

    private final String path;
    private final SubPhotoType type;

    PhotoFixture(String path, SubPhotoType type) {
        this.path = path;
        this.type = type;
    }

    public SubPhotoBuilder 기본_정보_생성() {
        return SubPhoto.builder()
                .path(this.path)
                .type(this.type);
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
