package nerds.studiousTestProject.review.repository;

import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static nerds.studiousTestProject.ReservationRecordFixture.*;
import static nerds.studiousTestProject.ReviewFixture.*;

@RepositoryTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRecordRepository reservationRecordRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private StudycafeRepository studycafeRepository;

    @Test
    void findAllById() {
        // given
        Studycafe studycafe = studycafeRepository.save(Studycafe.builder().id(1L).build());
        Room room = roomRepository.save(Room.builder().id(1L).studycafe(studycafe).build());
        Review review = reviewRepository.save(Review.builder().id(1L).createdDate(LocalDate.now()).build());
        ReservationRecord reservationRecord = reservationRecordRepository.save(ReservationRecord.builder().id(1L).room(room).build());
        reservationRecord.addReview(review);
        // when
        List<Review> reviewList = reviewRepository.findTop3ByReservationRecordId(studycafe.getId());
        // then
        Assertions.assertThat(reviewList).contains(review);
    }

    @Test
    void findAllByReservationRecordIdOrderByCreatedDatedDescIn() {
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
}