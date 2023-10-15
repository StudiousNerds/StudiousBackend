package nerds.studiousTestProject.photo.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nerds.studiousTestProject.support.EntitySaveProvider.권한_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.리뷰_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.스터디카페_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.회원_저장;
import static nerds.studiousTestProject.support.fixture.MemberFixture.KAKAO_USER;
import static nerds.studiousTestProject.support.fixture.PhotoFixture.REVIEW_PHOTO;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.TODAY_COMMENTED_REVIEW;
import static nerds.studiousTestProject.support.fixture.RoleFixture.ADMIN;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_FOUR_SIX;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static org.assertj.core.api.Assertions.*;

@RepositoryTest
class SubPhotoRepositoryTest {
    @Autowired
    private SubPhotoRepository subPhotoRepository;
    @Autowired
    private StudycafeRepository studycafeRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName(value = "리뷰 id로 리뷰 사진을 찾을 수 있다.")
    void findAllByReviewId() {
        // given
        Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        SubPhoto reviewPhoto1 = subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 1L));
        SubPhoto reviewPhoto2 = subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 2L));
        // when
        List<SubPhoto> reviewPhotoList = subPhotoRepository.findAllByReviewId(review.getId());
        // then
        assertThat(reviewPhotoList).contains(reviewPhoto1, reviewPhoto2);
    }

    @Test
    @DisplayName(value = "스터디카페 id로 스터디카페 사진을 찾을 수 있다.")
    void findAllByStudycafeId() {
        // given
        Member admin = 회원_저장(KAKAO_USER.생성());
        권한_저장(ADMIN.멤버_생성(admin));
        Studycafe studycafe = 스터디카페_저장(NERDS.멤버_생성(admin));
        SubPhoto studycafePhoto1 = subPhotoRepository.save(REVIEW_PHOTO.스터디카페_생성(studycafe, 1L));
        SubPhoto studycafePhoto2 = subPhotoRepository.save(REVIEW_PHOTO.스터디카페_생성(studycafe, 2L));
        // when
        List<SubPhoto> studycafePhotoList = subPhotoRepository.findAllByStudycafeId(studycafe.getId());
        // then
        assertThat(studycafePhotoList).contains(studycafePhoto1, studycafePhoto2);
    }

    @Test
    @DisplayName(value = "룸 id로 룸 사진을 찾을 수 있다.")
    void findAllByRoomId() {
        // given
        Member admin = 회원_저장(KAKAO_USER.생성());
        권한_저장(ADMIN.멤버_생성(admin));
        Studycafe studycafe = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room = roomRepository.save(ROOM_FOUR_SIX.스터디카페_생성(studycafe));
        SubPhoto roomPhoto1 = subPhotoRepository.save(REVIEW_PHOTO.룸_생성(room, 1L));
        SubPhoto roomphoto2 = subPhotoRepository.save(REVIEW_PHOTO.룸_생성(room, 2L));
        // when
        List<SubPhoto> studycafeRoomList = subPhotoRepository.findAllByRoomId(room.getId());
        // then
        assertThat(studycafeRoomList).contains(roomPhoto1, roomphoto2);
    }

    @Test
    @DisplayName(value = "리뷰 id로 리뷰 사진을 모두 삭제할 수 있다.")
    void deleteAllByReviewId() {
        // given
        Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 1L));
        subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 2L));
        // when
        subPhotoRepository.deleteAllByReviewId(review.getId());
        List<SubPhoto> photoList = subPhotoRepository.findAll();
        List<Review> reviewList = reviewRepository.findAll();
        // then
        assertThat(photoList).isEmpty();
        assertThat(reviewList).containsExactly(review);
    }

    @Test
    @DisplayName(value = "룸 id로 룸 사진을 모두 삭제할 수 있다.")
    void deleteAllByRoomId() {
        // given
        Member admin = 회원_저장(KAKAO_USER.생성());
        권한_저장(ADMIN.멤버_생성(admin));
        Studycafe studycafe = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room = roomRepository.save(ROOM_FOUR_SIX.스터디카페_생성(studycafe));
        subPhotoRepository.save(REVIEW_PHOTO.룸_생성(room, 1L));
        subPhotoRepository.save(REVIEW_PHOTO.룸_생성(room, 2L));
        // when
        subPhotoRepository.deleteAllByRoomId(room.getId());
        List<SubPhoto> photoList = subPhotoRepository.findAll();
        List<Room> roomList = roomRepository.findAll();
        // then
        assertThat(photoList).isEmpty();
        assertThat(roomList).containsExactly(room);
    }
}