package nerds.studiousTestProject.reservation.repository;

import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.fixture.ReviewFixture;
import nerds.studiousTestProject.fixture.StudycafeFixture;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.member.MemberRepository;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nerds.studiousTestProject.fixture.MemberFixture.*;
import static nerds.studiousTestProject.fixture.ReservationRecordFixture.*;
import static nerds.studiousTestProject.fixture.ReviewFixture.*;
import static nerds.studiousTestProject.fixture.RoomFixture.*;
import static nerds.studiousTestProject.fixture.StudycafeFixture.*;
import static org.assertj.core.api.Assertions.*;

@RepositoryTest
class ReservationRecordRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReservationRecordRepository reservationRecordRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudycafeRepository studycafeRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void findAllByRoomId() {
        // given
        Room room1 = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        ReservationRecord save1 = reservationRecordRepository.save(FIRST_RESERVATION.룸_생성(room1, 1L));
        ReservationRecord save2 = reservationRecordRepository.save(SECOND_RESERVATION.룸_생성(room1, 2L));
        // when
        List<ReservationRecord> reservationRecordList = reservationRecordRepository.findAllByRoomId(1L);
        // then
        assertThat(reservationRecordList).contains(save1, save2);
    }

    @Test
    void findAllByMemberId() {
        // given
        Member member = memberRepository.save(FIRST_MEMBER.생성(1L));
        ReservationRecord save1 = reservationRecordRepository.save(FIRST_RESERVATION.멤버_생성(member, 1L));
        ReservationRecord save2 = reservationRecordRepository.save(FIRST_RESERVATION.멤버_생성(member, 2L));
        // when
        List<ReservationRecord> reservationRecordList = reservationRecordRepository.findAllByMemberId(1L);
        // then
        assertThat(reservationRecordList).contains(save1, save2);
    }

    @Test
    void findAllByStudycafeId() {
        // given
        Studycafe studycafe = studycafeRepository.save(FIRST_STUDYCAFE.생성(1L));
        Room room1 = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        studycafe.addRoom(room1);
        Review review = reviewRepository.save(FIRST_REVIEW.생성(1L));
        ReservationRecord save1 = reservationRecordRepository.save(FIRST_RESERVATION.룸_생성(room1, 1L));
        ReservationRecord save2 = reservationRecordRepository.save(SECOND_RESERVATION.룸_생성(room1, 2L));
        save1.addReview(review);
        // when
        List<ReservationRecord> reservationRecordList = reservationRecordRepository.findAllByStudycafeId(studycafe.getId());
        // then
        assertThat(reservationRecordList).contains(save1);
    }
}