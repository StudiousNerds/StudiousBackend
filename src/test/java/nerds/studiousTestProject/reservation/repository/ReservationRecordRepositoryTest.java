package nerds.studiousTestProject.reservation.repository;

import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.member.MemberRepository;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus;
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

import static nerds.studiousTestProject.support.fixture.MemberFixture.BEAVER;
import static nerds.studiousTestProject.support.fixture.MemberFixture.BURNED_POTATO;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CANCELED_RESERVATION;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CONFIRM_RESERVATION;
import static nerds.studiousTestProject.support.fixture.RoomFixture.*;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static nerds.studiousTestProject.fixture.MemberFixture.*;
import static nerds.studiousTestProject.fixture.ReservationRecordFixture.*;
import static nerds.studiousTestProject.fixture.ReviewFixture.*;
import static nerds.studiousTestProject.fixture.StudycafeFixture.*;

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

    @Test
    @DisplayName(value = "회원이 주어졌을 때 해당 회원의 예약 목록을 페이징해 조회할 수 있다.")
    void 예약_목록_전체를_페이징하여_조회한다() {

        Member member1 = 멤버_저장(BEAVER.생성(1L));
        Member member2 = 멤버_저장(BURNED_POTATO.생성(2L));
        Studycafe studycafe = 스터디카페_저장(NERDS.생성());
        Room room = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe));

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

        Member member1 = 멤버_저장(BEAVER.생성(1L));
        Studycafe studycafe = 스터디카페_저장(NERDS.생성());
        Room room = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe));

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