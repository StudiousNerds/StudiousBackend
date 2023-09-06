package nerds.studiousTestProject.photo.repository;

import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nerds.studiousTestProject.support.fixture.PhotoFixture.*;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.*;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.*;
import static nerds.studiousTestProject.support.fixture.RoomFixture.*;
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
    void findAllByReviewId() {
        // given
        Review review = reviewRepository.save(TODAY_COMMENTED_REVIEW.기본_정보_생성(1L));
        SubPhoto subPhoto = subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 1L));
        SubPhoto subPhoto1 = subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 2L));
        // when
        List<SubPhoto> reviewPhotoList = subPhotoRepository.findAllByReviewId(review.getId());
        // then
        assertThat(reviewPhotoList).contains(subPhoto, subPhoto1);
    }

    @Test
    void findAllByStudycafeId() {
        // given
        Studycafe studycafe = studycafeRepository.save(FIRST_STUDYCAFE.생성(1L));
        SubPhoto subPhoto = subPhotoRepository.save(REVIEW_PHOTO.스터디카페_생성(studycafe, 1L));
        SubPhoto subPhoto1 = subPhotoRepository.save(REVIEW_PHOTO.스터디카페_생성(studycafe, 2L));
        // when
        List<SubPhoto> studycafePhotoList = subPhotoRepository.findAllByStudycafeId(1L);
        // then
        assertThat(studycafePhotoList).contains(subPhoto, subPhoto1);
    }

    @Test
    void findAllByRoomId() {
        // given
        Room room = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        SubPhoto subPhoto = subPhotoRepository.save(REVIEW_PHOTO.룸_생성(room, 1L));
        SubPhoto subPhoto1 = subPhotoRepository.save(REVIEW_PHOTO.룸_생성(room, 2L));
        // when
        List<SubPhoto> studycafeRoomList = subPhotoRepository.findAllByRoomId(room.getId());
        // then
        assertThat(studycafeRoomList).contains(subPhoto, subPhoto1);
    }

    @Test
    void deleteAllByReviewId() {
        // given
        Review review = reviewRepository.save(TODAY_COMMENTED_REVIEW.기본_정보_생성(1L));
        SubPhoto subPhoto = subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 1L));
        SubPhoto subPhoto1 = subPhotoRepository.save(REVIEW_PHOTO.리뷰_생성(review, 2L));
        // when
        subPhotoRepository.deleteAllByReviewId(review.getId());
        List<SubPhoto> photoList = subPhotoRepository.findAll();
        List<Review> reviewList = reviewRepository.findAll();
        // then
        assertThat(photoList.size()).isEqualTo(0);
        assertThat(reviewList.size()).isEqualTo(1);
    }
}