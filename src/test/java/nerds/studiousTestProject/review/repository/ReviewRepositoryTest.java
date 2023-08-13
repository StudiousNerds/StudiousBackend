package nerds.studiousTestProject.review.repository;
import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.fixture.HashtagFixture;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.repository.HashtagRepository;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static nerds.studiousTestProject.fixture.GradeFixture.*;
import static nerds.studiousTestProject.fixture.HashtagFixture.*;
import static nerds.studiousTestProject.fixture.ReservationRecordFixture.*;
import static nerds.studiousTestProject.fixture.ReviewFixture.*;

@RepositoryTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRecordRepository reservationRecordRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private HashtagRepository hashtagRepository;

    @Test
    @DisplayName("예약 id가 주어졌을 때 생성날짜를 기준으로 내림차순 정렬하여 그에 해당하는 리뷰들을 모두 다 가져오는지 테스트")
    void findAllByReservationRecordIdInOrderByCreatedDatedDesc() {
        // given
        Review firstReview = reviewRepository.save(FIRST_REVIEW.생성(1L));
        Review secondReview = reviewRepository.save(SECOND_REVIEW.생성(2L));
        Review thirdReview = reviewRepository.save(THIRD_REVIEW.생성(3L));
        reservationRecordRepository.save(FIRST_RESERVATION.생성(1L)).addReview(firstReview);
        reservationRecordRepository.save(SECOND_RESERVATION.생성(2L)).addReview(secondReview);
        reservationRecordRepository.save(THIRD_RESERVATION.생성(3L)).addReview(thirdReview);
        // when
        List<Review> reviewList = reviewRepository.findAllByReservationRecordIdInOrderByCreatedDateDesc(Arrays.asList(1L, 2L, 3L));
        // then
        Assertions.assertThat(reviewList).containsExactly(firstReview, secondReview, thirdReview);
    }

    @Test
    @DisplayName("예약 id가 여러개 주어졌을 때 그에 해당하는 리뷰들을 모두 다 가져오는지 테스트")
    void findAllByReservationRecordIdIn() {
        // given
        Grade firstGrade = gradeRepository.save(FIRST_GRADE.생성(1L));
        Grade secondGrade = gradeRepository.save(SECOND_GRADE.생성(2L));
        Grade thirdGrade = gradeRepository.save(THIRD_GRADE.생성(3L));
        Review firstReview = reviewRepository.save(FIRST_REVIEW.평점_생성(firstGrade, 1L));
        Review secondReview = reviewRepository.save(FIRST_REVIEW.평점_생성(secondGrade, 2L));
        Review thirdReview = reviewRepository.save(FIRST_REVIEW.평점_생성(thirdGrade, 3L));
        reservationRecordRepository.save(FIRST_RESERVATION.생성(1L)).addReview(firstReview);
        reservationRecordRepository.save(SECOND_RESERVATION.생성(2L)).addReview(secondReview);
        reservationRecordRepository.save(THIRD_RESERVATION.생성(3L)).addReview(thirdReview);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("grade.total").descending());
        // when
        Page<Review> reviewPage = reviewRepository.findAllByReservationRecordIdIn(Arrays.asList(1L, 2L, 3L), pageable);
        List<Review> reviewList = reviewPage.getContent();
        // then
        Assertions.assertThat(reviewList).containsExactly(firstReview, secondReview, thirdReview);
    }

    @Test
    @DisplayName("예약 내역 id로 리뷰를 찾는데 생성일자를 내림차순으로 정렬했을 때 상위 3개를 가져오는지 테스트")
    void findTop3ByReservationRecordId() {
        // given
        Review firstReview = reviewRepository.save(FIRST_REVIEW.생성(1L));
        Review secondReview = reviewRepository.save(SECOND_REVIEW.생성(2L));
        Review thirdReview = reviewRepository.save(THIRD_REVIEW.생성(3L));
        reservationRecordRepository.save(FIRST_RESERVATION.생성(1L)).addReview(firstReview);
        reservationRecordRepository.save(SECOND_RESERVATION.생성(2L)).addReview(secondReview);
        reservationRecordRepository.save(THIRD_RESERVATION.생성(3L)).addReview(thirdReview);
        // when
        List<Review> reviewList = reviewRepository.findTop3ByReservationRecordId(Arrays.asList(1L, 2L, 3L));
        // then
        Assertions.assertThat(reviewList).containsExactly(firstReview, secondReview, thirdReview);
    }

    @Test
    @DisplayName("리뷰 삭제 시, 해시태그와 등급이 같이 삭제되는지 테스트")
    void deleteById() {
        // given
        HashtagRecord hashtag = hashtagRepository.save(FIRST_HASHTAG.생성(1L));
        Grade firstGrade = gradeRepository.save(FIRST_GRADE.생성(1L));
        Review firstReview = reviewRepository.save(FIRST_REVIEW.평점_생성(firstGrade, 1L));
        firstReview.addHashtagRecord(hashtag);
        // when
        reviewRepository.deleteById(1L);
        List<Review> reviewList = reviewRepository.findAll();
        List<Grade> gradeList = gradeRepository.findAll();
        List<HashtagRecord> hashtagRecordList = hashtagRepository.findAll();
        // then
        Assertions.assertThat(reviewList.size()).isEqualTo(0);
        Assertions.assertThat(gradeList.size()).isEqualTo(0);
        Assertions.assertThat(hashtagRecordList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("제대로 review에 반영되어 저장되었는지 확인하는 테스트")
    void saveTest() {
        // given
        HashtagRecord hashtag = hashtagRepository.save(FIRST_HASHTAG.생성(1L));
        Grade firstGrade = gradeRepository.save(FIRST_GRADE.생성(1L));
        Review firstReview = reviewRepository.save(FIRST_REVIEW.평점_생성(firstGrade, 1L));
        firstReview.addHashtagRecord(hashtag);
        // when
        List<Review> reviewList = reviewRepository.findAll();
        List<Grade> gradeList = gradeRepository.findAll();
        List<HashtagRecord> hashtagRecordList = hashtagRepository.findAll();
        // then
        Assertions.assertThat(reviewList.size()).isEqualTo(1);
        Assertions.assertThat(gradeList.size()).isEqualTo(1);
        Assertions.assertThat(hashtagRecordList.size()).isEqualTo(1);
    }
}