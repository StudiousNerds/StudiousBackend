package nerds.studiousTestProject.review.repository;

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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        ReservationRecord reservationRecord = reservationRecordRepository.save(ReservationRecord.builder().id(1L).room(room).build());
        Review review = reviewRepository.save(Review.builder().id(1L).reservationRecord(reservationRecord).createdDate(LocalDate.now()).build());
        // when
        List<Review> reviewList = reviewRepository.findAllById(studycafe.getId());
        // then
        Assertions.assertThat(reviewList).contains(review);
    }
}