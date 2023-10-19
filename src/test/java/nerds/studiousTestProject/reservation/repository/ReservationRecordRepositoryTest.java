package nerds.studiousTestProject.reservation.repository;

import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CANCELED_RESERVATION;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CONFIRM_RESERVATION;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.IN_PROGRESS_RESERVATION;
import static nerds.studiousTestProject.support.fixture.RoomFixture.*;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.*;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        Room room1 = roomRepository.save(ROOM_FOUR_SIX.생성(2L));
        ReservationRecord save1 = reservationRecordRepository.save(CONFIRM_RESERVATION.룸_생성(room1, 1L));
        ReservationRecord save2 = reservationRecordRepository.save(IN_PROGRESS_RESERVATION.룸_생성(room1, 2L));
        // when
        List<ReservationRecord> reservationRecordList = reservationRecordRepository.findAllByRoomId(2L);
        // then
        assertThat(reservationRecordList).contains(save1, save2);
    }

//    @Test
//    void findAllByMemberId() {
//        // given
//        Member member = memberRepository.save(POTATO.생성(3L));
//        ReservationRecord save1 = reservationRecordRepository.save(CONFIRM_RESERVATION.멤버_생성(member, 7L));
//        ReservationRecord save2 = reservationRecordRepository.save(IN_PROGRESS_RESERVATION.멤버_생성(member, 8L));
//        // when
//        List<ReservationRecord> reservationRecordList = reservationRecordRepository.findAllByMemberId(member.getId());
//        // then
//        assertThat(reservationRecordList).contains(save1, save2);
//    }

    @Test
    void findAllByStudycafeId() {
        // given
        Studycafe studycafe = studycafeRepository.save(FIRST_STUDYCAFE.생성(1L));
        Room room1 = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        studycafe.addRoom(room1);
        Review review = reviewRepository.save(TODAY_COMMENTED_REVIEW.기본_정보_생성(1L));
        ReservationRecord save1 = reservationRecordRepository.save(CONFIRM_RESERVATION.룸_생성(room1, 1L));
        ReservationRecord save2 = reservationRecordRepository.save(IN_PROGRESS_RESERVATION.룸_생성(room1, 2L));
//        save1.addReview(review);
        // when
        List<ReservationRecord> reservationRecordList = reservationRecordRepository.findAllByStudycafeId(studycafe.getId());
        // then
        assertThat(reservationRecordList).contains(save1);
    }

    @Test
    @DisplayName(value = "회원이 주어졌을 때 해당 회원의 예약 목록을 페이징해 조회할 수 있다.")
    void 예약_목록_전체를_페이징하여_조회한다() {

        Member member1 = 멤버_저장(DEFAULT_USER.생성(1L));
        Member member2 = 멤버_저장(DEFAULT_USER.생성(2L));
        Room room = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(NERDS.생성())));

        예약_내역_저장(CANCELED_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(12, 0), LocalTime.of(14, 0), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(16, 0), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(16, 0), LocalTime.of(18, 0), member1, room));
        ReservationRecord reservation1 = 예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(19, 0), LocalTime.of(21, 0), member1, room));
        ReservationRecord reservation2 = 예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(22, 0), LocalTime.of(23, 0), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(19, 0), LocalTime.of(20, 0), member2, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(22, 0), LocalTime.of(12, 0), member2, room));
        Pageable pageable = PageRequest.of(0, 2);
        Page<ReservationRecord> page = reservationRecordRepository.getReservationRecordsConditions(ReservationSettingsStatus.ALL, null, null, null, member1, pageable);
        assertAll(
                ()->assertThat(page.getTotalPages()).isEqualTo(3),
                ()->assertThat(page.getContent()).containsExactly(reservation2, reservation1)
        );
    }

    @Test
    void 예약_취소_탭을_페이징해_조회한다(){

        Member member1 = 멤버_저장(DEFAULT_USER.생성(1L));
        Room room = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(NERDS.멤버_생성(member1))));

        ReservationRecord reservation1 = 예약_내역_저장(CANCELED_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(12, 0), LocalTime.of(14, 0), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(16, 0), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(22, 0), LocalTime.of(23, 0), member1, room));
        Pageable pageable = PageRequest.of(0, 1);
        Page<ReservationRecord> page = reservationRecordRepository.getReservationRecordsConditions(ReservationSettingsStatus.CANCELED, null, null, null, member1, pageable);
        assertAll(
                ()->assertThat(page.getTotalPages()).isEqualTo(1),
                ()->assertThat(page.getContent()).containsExactly(reservation1)
        );
    }

    @Test
    @DisplayName("지난 예약 탭을 페이징 해 조회할 수 있다.")
    public void 지난_예약_탭_조회() {
        Member member1 = 멤버_저장(DEFAULT_USER.생성(1L));
        Room room = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(NERDS.멤버_생성(member1))));

        예약_내역_저장(CANCELED_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.of(10,0), LocalTime.now(), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now().minusMonths(1), LocalTime.now(), LocalTime.now(), member1, room));
        예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now().plusMonths(1), LocalTime.now(), LocalTime.now(), member1, room));
        ReservationRecord reservation = 예약_내역_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(2), member1, room));
        Pageable pageable = PageRequest.of(0, 1);
        Page<ReservationRecord> page = reservationRecordRepository.getReservationRecordsConditions(ReservationSettingsStatus.BEFORE_USING, null, null, null, member1, pageable);

        assertAll(
                ()->assertThat(page.getTotalPages()).isEqualTo(1),
                ()->assertThat(page.getContent()).containsExactly(reservation)
        );
    }


    private Room 룸_저장(Room room) {
        return roomRepository.save(room);
    }

    private Member 멤버_저장(Member member) {
        return memberRepository.save(member);
    }

    private ReservationRecord 예약_내역_저장(ReservationRecord reservationRecord) {
        return reservationRecordRepository.save(reservationRecord);
    }

    private Studycafe 스터디카페_저장(Studycafe studycafe) {
        return studycafeRepository.save(studycafe);
    }
}