package nerds.studiousTestProject.review.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.review.dto.manage.inquire.request.AdminReviewType;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.support.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static nerds.studiousTestProject.support.EntitySaveProvider.룸_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.리뷰_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.스터디카페_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.예약_정보_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.회원_저장;
import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CONFIRM_RESERVATION;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.TODAY_COMMENTED_REVIEW;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.TODAY_NO_COMMENTED_REVIEW;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.YESTERDAY_COMMENTED_REVIEW;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.TWO_DAYS_AGO_COMMENTED_REVIEW;
import static nerds.studiousTestProject.support.fixture.RoleFixture.ADMIN;
import static nerds.studiousTestProject.support.fixture.RoleFixture.USER;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_FOUR_SIX;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class ReviewRepositoryCustomImplTest {
    @Autowired
    ReviewRepository reviewRepository;

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

        Pageable pageable = PageRequestConverter.of(1, 100, null);

        // when
        List<Review> reviews1 = reviewRepository.getPagedReviewsByStudycafeId(studycafe1.getId(), AdminReviewType.ALL, pageable).getContent();
        List<Review> reviews2 = reviewRepository.getPagedReviewsByStudycafeId(studycafe2.getId(), AdminReviewType.ALL, pageable).getContent();

        // then
        assertThat(reviews1.size()).isEqualTo(93);
        assertThat(reviews2.size()).isEqualTo(90);
    }

    @Test
    @DisplayName("페이징 처리 하여 30개 목록만 보여줌")
    public void 특정_스터디카페_모든_리뷰_조회_페이징() throws Exception {

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

        Pageable pageable = PageRequestConverter.of(1, 30, null);

        // when
        List<Review> reviews1 = reviewRepository.getPagedReviewsByStudycafeId(studycafe1.getId(), AdminReviewType.ALL, pageable).getContent();
        List<Review> reviews2 = reviewRepository.getPagedReviewsByStudycafeId(studycafe2.getId(), AdminReviewType.ALL, pageable).getContent();

        // then
        assertThat(reviews1.size()).isEqualTo(30);
        assertThat(reviews2.size()).isEqualTo(30);
    }

    @Test
    @DisplayName("정렬 조건 생략 시 최신순 정렬")
    public void 특정_스터디카페_모든_리뷰_조회_정렬_조건_X() throws Exception {

        // given
        Member admin = 회원_저장(DEFAULT_USER.생성());
        ADMIN.멤버_생성(admin);

        Member member = 회원_저장(DEFAULT_USER.생성());
        USER.멤버_생성(member);

        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        LocalDate currentReserveDate = LocalDate.of(2023, 7, 1);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(19, 0);

        Review review1 = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room1, review1));

        Review review2 = 리뷰_저장(YESTERDAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room2, review2));

        Review review3 = 리뷰_저장(TWO_DAYS_AGO_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room3, review3));

        Pageable pageable = PageRequestConverter.of(1, 100, Sort.unsorted());

        // when
        List<Review> reviews = reviewRepository.getPagedReviewsByStudycafeId(studycafe1.getId(), AdminReviewType.ALL, pageable).getContent();

        // then
        assertThat(reviews.get(0).getCreatedDate()).isEqualTo(TODAY_COMMENTED_REVIEW.getCreatedDate());
        assertThat(reviews.get(1).getCreatedDate()).isEqualTo(YESTERDAY_COMMENTED_REVIEW.getCreatedDate());
        assertThat(reviews.get(2).getCreatedDate()).isEqualTo(TWO_DAYS_AGO_COMMENTED_REVIEW.getCreatedDate());
    }

    @Test
    @DisplayName("최신순 특정 스터디카페 모든 리뷰 조회")
    public void 특정_스터디카페_모든_리뷰_조회_최신순() throws Exception {

        // given
        Member admin = 회원_저장(DEFAULT_USER.생성());
        ADMIN.멤버_생성(admin);

        Member member = 회원_저장(DEFAULT_USER.생성());
        USER.멤버_생성(member);

        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        LocalDate currentReserveDate = LocalDate.of(2023, 7, 1);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(19, 0);

        Review review1 = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room1, review1));

        Review review2 = 리뷰_저장(YESTERDAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room2, review2));

        Review review3 = 리뷰_저장(TWO_DAYS_AGO_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room3, review3));

        Pageable pageable = PageRequestConverter.of(1, 100, Sort.by(Sort.Direction.DESC, "createdDate"));

        // when
        List<Review> reviews = reviewRepository.getPagedReviewsByStudycafeId(studycafe1.getId(), AdminReviewType.ALL, pageable).getContent();

        // then
        assertThat(reviews.get(0).getCreatedDate()).isEqualTo(TODAY_COMMENTED_REVIEW.getCreatedDate());
        assertThat(reviews.get(1).getCreatedDate()).isEqualTo(YESTERDAY_COMMENTED_REVIEW.getCreatedDate());
        assertThat(reviews.get(2).getCreatedDate()).isEqualTo(TWO_DAYS_AGO_COMMENTED_REVIEW.getCreatedDate());
    }

    @Test
    @DisplayName("오래된 순 특정 스터디카페 모든 리뷰 조회")
    public void 특정_스터디카페_모든_리뷰_조회_오래된_순() throws Exception {

        // given
        Member admin = 회원_저장(DEFAULT_USER.생성());
        ADMIN.멤버_생성(admin);

        Member member = 회원_저장(DEFAULT_USER.생성());
        USER.멤버_생성(member);

        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        LocalDate currentReserveDate = LocalDate.of(2023, 7, 1);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(19, 0);

        Review review1 = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room1, review1));

        Review review2 = 리뷰_저장(YESTERDAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room2, review2));

        Review review3 = 리뷰_저장(TWO_DAYS_AGO_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room3, review3));

        Pageable pageable = PageRequestConverter.of(1, 100, Sort.by(Sort.Direction.ASC, "createdDate"));

        // when
        List<Review> reviews = reviewRepository.getPagedReviewsByStudycafeId(studycafe1.getId(), AdminReviewType.ALL, pageable).getContent();

        // then
        assertThat(reviews.get(2).getCreatedDate()).isEqualTo(TODAY_COMMENTED_REVIEW.getCreatedDate());
        assertThat(reviews.get(1).getCreatedDate()).isEqualTo(YESTERDAY_COMMENTED_REVIEW.getCreatedDate());
        assertThat(reviews.get(0).getCreatedDate()).isEqualTo(TWO_DAYS_AGO_COMMENTED_REVIEW.getCreatedDate());
    }

    @Test
    @DisplayName("댓글달지 않은 특정 스터디카페 모든 리뷰 조회")
    public void 특정_스터디카페_모든_리뷰_조회_미답변() throws Exception {

        // given
        Member admin = 회원_저장(DEFAULT_USER.생성());
        ADMIN.멤버_생성(admin);

        Member member = 회원_저장(DEFAULT_USER.생성());
        USER.멤버_생성(member);

        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        LocalDate currentReserveDate = LocalDate.of(2023, 7, 1);
        LocalTime startTime = LocalTime.of(17, 0);
        LocalTime endTime = LocalTime.of(19, 0);

        Review review1 = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room1, review1));

        Review review2 = 리뷰_저장(TODAY_NO_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room2, review2));

        Review review3 = 리뷰_저장(TODAY_NO_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        예약_정보_저장(CONFIRM_RESERVATION.리뷰_추가_생성(currentReserveDate, startTime, endTime, member, room3, review3));

        Pageable pageable = PageRequestConverter.of(1, 100, Sort.by(Sort.Direction.ASC, "createdDate"));

        // when
        List<Review> reviews = reviewRepository.getPagedReviewsByStudycafeId(studycafe1.getId(), AdminReviewType.NO_ANSWER, pageable).getContent();

        // then
        assertThat(reviews.size()).isEqualTo(2);
    }
}