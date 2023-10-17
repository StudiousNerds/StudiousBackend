package nerds.studiousTestProject.review.repository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.review.entity.Review;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static nerds.studiousTestProject.support.EntitySaveProvider.룸_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.스터디카페_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.예약_정보_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.회원_저장;
import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CONFIRM_RESERVATION;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.TODAY_COMMENTED_REVIEW;
import static nerds.studiousTestProject.support.fixture.RoleFixture.ADMIN;
import static nerds.studiousTestProject.support.fixture.RoleFixture.USER;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_FOUR_SIX;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;

@RepositoryTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("특정 스터디카페의 모든 리뷰를 조회하는지 테스트")
    public void 특정_스터디카페_모든_리뷰_조회() throws Exception {

        // given
        Member admin = 회원_저장(DEFAULT_USER.생성());
        ADMIN.멤버_생성(admin);

        Member member = 회원_저장(DEFAULT_USER.생성());
        USER.멤버_생성(member);

        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        LocalDate currentReserveDate1 = LocalDate.of(2023, 7, 1);
        LocalTime startTime1 = LocalTime.of(17, 0);
        LocalTime endTime1 = LocalTime.of(19, 0);

        for (long i = 0L; i < 31L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate1.plusDays(i), startTime1, endTime1, member, room1, review));
        }

        for (long i = 0L; i < 31L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate1.plusDays(i), startTime1, endTime1, member, room2, review));
        }

        for (long i = 0L; i < 31L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate1.plusDays(i), startTime1, endTime1, member, room3, review));
        }

        Studycafe studycafe2 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room4 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));
        Room room5 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));
        Room room6 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));

        LocalDate currentReserveDate2 = LocalDate.of(2023, 6, 1);
        LocalTime startTime2 = LocalTime.of(17, 0);
        LocalTime endTime2 = LocalTime.of(19, 0);

        for (long i = 0L; i < 30L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate2.plusDays(i), startTime2, endTime2, member, room4, review));
        }

        for (long i = 0L; i < 30L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate2.plusDays(i), startTime2, endTime2, member, room5, review));
        }

        for (long i = 0L; i < 30L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate2.plusDays(i), startTime2, endTime2, member, room6, review));
        }

        // when
        List<Review> reviews1 = reviewRepository.findAllByStudycafeId(studycafe1.getId(), null).getContent();
        List<Review> reviews2 = reviewRepository.findAllByStudycafeId(studycafe2.getId(), null).getContent();

        // then
        Assertions.assertThat(reviews1.size()).isEqualTo(93);
        Assertions.assertThat(reviews2.size()).isEqualTo(90);
    }

    @Test
    @DisplayName("특정 스터디룸의 모든 리뷰를 조회하는지 테스트")
    public void 특정_스터디룸_모든_리뷰_조회() throws Exception {

        // given
        Member admin = 회원_저장(DEFAULT_USER.생성());
        ADMIN.멤버_생성(admin);

        Member member = 회원_저장(DEFAULT_USER.생성());
        USER.멤버_생성(member);

        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        LocalDate currentReserveDate1 = LocalDate.of(2023, 7, 1);
        LocalTime startTime1 = LocalTime.of(17, 0);
        LocalTime endTime1 = LocalTime.of(19, 0);

        Review room1Review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate1, startTime1, endTime1, member, room1, room1Review));

        Studycafe studycafe2 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));

        LocalDate currentReserveDate2 = LocalDate.of(2023, 6, 1);
        LocalTime startTime2 = LocalTime.of(17, 0);
        LocalTime endTime2 = LocalTime.of(19, 0);

        Review room2Review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate2, startTime2, endTime2, member, room2, room2Review));

        // when
        List<Review> reviews1 = reviewRepository.findAllByRoomId(room1.getId(), null).getContent();
        List<Review> reviews2 = reviewRepository.findAllByRoomId(room2.getId(), null).getContent();

        // then
        Assertions.assertThat(reviews1).containsExactly(room1Review);
        Assertions.assertThat(reviews2).containsExactly(room2Review);
    }

    private Review 리뷰_저장(Review review) {
        return reviewRepository.save(review);
    }
}