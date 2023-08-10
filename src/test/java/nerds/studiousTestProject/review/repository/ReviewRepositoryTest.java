package nerds.studiousTestProject.review.repository;
import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static nerds.studiousTestProject.GradeFixture.*;
import static nerds.studiousTestProject.ReservationRecordFixture.*;
import static nerds.studiousTestProject.ReviewFixture.*;

@RepositoryTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRecordRepository reservationRecordRepository;
    @Autowired
    private GradeRepository gradeRepository;

    @Test
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
}