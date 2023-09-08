package nerds.studiousTestProject.review.repository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.entity.Review;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static nerds.studiousTestProject.support.EntitySaveProvider.룸_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.리뷰_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.스터디카페_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.예약_정보_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.회원_저장;
import static nerds.studiousTestProject.support.fixture.MemberFixture.*;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.*;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.*;
import static nerds.studiousTestProject.support.fixture.RoleFixture.*;
import static nerds.studiousTestProject.support.fixture.RoomFixture.*;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.*;

@RepositoryTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRecordRepository reservationRecordRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private HashtagRecordRepository hashtagRecordRepository;

//    @Test
//    @DisplayName("예약 id가 주어졌을 때 생성날짜를 기준으로 내림차순 정렬하여 그에 해당하는 리뷰들을 모두 다 가져오는지 테스트")
//    void findAllByReservationRecordIdInOrderByCreatedDatedDesc() {
//        // given
//        Review firstReview = reviewRepository.save(TODAY_COMMENTED_REVIEW.기본_정보_생성(1L));
//        Review secondReview = reviewRepository.save(YESTERDAY_COMMENTED_REVIEW.기본_정보_생성(2L));
//        Review thirdReview = reviewRepository.save(TWO_DAYS_AGO_COMMENTED_REVIEW.기본_정보_생성(3L));
//        reservationRecordRepository.save(CONFIRM_RESERVATION.기본_정보_생성(1L)).addReview(firstReview);
//        reservationRecordRepository.save(IN_PROGRESS_RESERVATION.기본_정보_생성(2L)).addReview(secondReview);
//        reservationRecordRepository.save(CANCELED_RESERVATION.기본_정보_생성(3L)).addReview(thirdReview);
//        // when
//        List<Review> reviewList = reviewRepository.findAllByIdInOrderByCreatedDateDesc(
//                Arrays.asList(firstReview.getId(), secondReview.getId(), thirdReview.getId()));
//        // then
//        Assertions.assertThat(reviewList).containsExactly(firstReview, secondReview, thirdReview);
//    }
//
//    @Test
//    @DisplayName("예약 id가 여러개 주어졌을 때 그에 해당하는 리뷰들을 모두 다 가져오는지 테스트")
//    void findAllByReservationRecordIdIn() {
//        // given
//        Grade firstGrade = gradeRepository.save(NOT_RECOMMENDED_TOTAL_FIVE_GRADE.기본_정보_생성(1L));
//        Grade secondGrade = gradeRepository.save(RECOMMENDED_TOTAL_THREE_GRADE.기본_정보_생성(2L));
//        Grade thirdGrade = gradeRepository.save(RECOMMENDED_TOTAL_ONE_GRADE.기본_정보_생성(3L));
//        Review firstReview = reviewRepository.save(TODAY_COMMENTED_REVIEW.평점_생성(firstGrade, 1L));
//        Review secondReview = reviewRepository.save(YESTERDAY_COMMENTED_REVIEW.평점_생성(secondGrade, 2L));
//        Review thirdReview = reviewRepository.save(TWO_DAYS_AGO_COMMENTED_REVIEW.평점_생성(thirdGrade, 3L));
//        reservationRecordRepository.save(CONFIRM_RESERVATION.기본_정보_생성(1L)).addReview(firstReview);
//        reservationRecordRepository.save(IN_PROGRESS_RESERVATION.기본_정보_생성(2L)).addReview(secondReview);
//        reservationRecordRepository.save(CANCELED_RESERVATION.기본_정보_생성(3L)).addReview(thirdReview);
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("grade.total").descending());
//        // when
//        Page<Review> reviewPage = reviewRepository.findAllByIdIn(Arrays.asList(
//                firstReview.getId(), secondReview.getId(), thirdReview.getId()), pageable);
//        List<Review> reviewList = reviewPage.getContent();
//        // then
//        Assertions.assertThat(reviewList).containsExactly(firstReview, secondReview, thirdReview);
//    }
//
//    @Test
//    @DisplayName("리뷰 삭제 시, 해시태그와 등급이 같이 삭제되는지 테스트")
//    void deleteById() {
//        // given
//        HashtagRecord hashtag = hashtagRecordRepository.save(COST_EFFECTIVE_HASHTAG.기본_정보_생성(1L));
//        Grade firstGrade = gradeRepository.save(NOT_RECOMMENDED_TOTAL_FIVE_GRADE.기본_정보_생성(1L));
//        Review firstReview = reviewRepository.save(TODAY_COMMENTED_REVIEW.평점_생성(firstGrade, 1L));
//        firstReview.addHashtagRecord(hashtag);
//        // when
//        reviewRepository.deleteById(firstReview.getId());
//        List<Review> reviewList = reviewRepository.findAll();
//        List<Grade> gradeList = gradeRepository.findAll();
//        List<HashtagRecord> hashtagRecordList = hashtagRecordRepository.findAll();
//        // then
//        Assertions.assertThat(reviewList.size()).isEqualTo(0);
//        Assertions.assertThat(gradeList.size()).isEqualTo(0);
//        Assertions.assertThat(hashtagRecordList.size()).isEqualTo(0);
//    }
//
//    @Test
//    @DisplayName("제대로 review에 반영되어 저장되었는지 확인하는 테스트")
//    void saveTest() {
//        // given
//        HashtagRecord hashtag = hashtagRecordRepository.save(COST_EFFECTIVE_HASHTAG.기본_정보_생성(1L));
//        Grade firstGrade = gradeRepository.save(NOT_RECOMMENDED_TOTAL_FIVE_GRADE.기본_정보_생성(1L));
//        Review firstReview = reviewRepository.save(TODAY_COMMENTED_REVIEW.평점_생성(firstGrade, 1L));
//        firstReview.addHashtagRecord(hashtag);
//        // when
//        List<Review> reviewList = reviewRepository.findAll();
//        List<Grade> gradeList = gradeRepository.findAll();
//        List<HashtagRecord> hashtagRecordList = hashtagRecordRepository.findAll();
//        // then
//        Assertions.assertThat(reviewList.size()).isEqualTo(1);
//        Assertions.assertThat(gradeList.size()).isEqualTo(1);
//        Assertions.assertThat(hashtagRecordList.size()).isEqualTo(1);
//    }

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
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(currentReserveDate1.plusDays(i), startTime1, endTime1, member, room1, review));
        }

        for (long i = 0L; i < 31L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(currentReserveDate1.plusDays(i), startTime1, endTime1, member, room2, review));
        }

        for (long i = 0L; i < 31L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(currentReserveDate1.plusDays(i), startTime1, endTime1, member, room3, review));
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
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(currentReserveDate2.plusDays(i), startTime2, endTime2, member, room4, review));
        }

        for (long i = 0L; i < 30L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(currentReserveDate2.plusDays(i), startTime2, endTime2, member, room5, review));
        }

        for (long i = 0L; i < 30L; i++) {
            Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(currentReserveDate2.plusDays(i), startTime2, endTime2, member, room6, review));
        }

        // when
        List<Review> reviews1 = reviewRepository.findAllByStudycafeId(studycafe1.getId(), null).getContent();
        List<Review> reviews2 = reviewRepository.findAllByStudycafeId(studycafe2.getId(), null).getContent();

        // then
        Assertions.assertThat(reviews1.size()).isEqualTo(93);
        Assertions.assertThat(reviews2.size()).isEqualTo(90);
    }
}