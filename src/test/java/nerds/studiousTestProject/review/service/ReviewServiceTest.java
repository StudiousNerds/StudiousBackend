package nerds.studiousTestProject.review.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.repository.AccumHashtagHistoryRepository;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.dto.modify.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.modify.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.register.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.GradeRepository;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CONFIRM_RESERVATION;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.YESTERDAY_COMMENTED_REVIEW;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_TWO_FOUR;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ReservationRecordRepository reservationRecordRepository;

    @Mock
    StudycafeRepository studycafeRepository;

    @Mock
    GradeRepository gradeRepository;

    @Mock
    AccumHashtagHistoryRepository accumHashtagHistoryRepository;

    @Mock
    HashtagRecordRepository hashtagRecordRepository;

    @Mock
    SubPhotoRepository subPhotoRepository;

    @Mock
    StorageProvider storageProvider;


    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("리뷰를 등록할 수 있다.")
    void register() {
        // given
        Member member = DEFAULT_USER.생성();
        Studycafe studycafe = NERDS.생성();
        Room room = ROOM_TWO_FOUR.스터디카페_생성(studycafe);
        ReservationRecord reservationRecord = CONFIRM_RESERVATION.예약_내역_생성(
                LocalDate.of(2013,10,12),
                LocalTime.of(16,0,0),
                LocalTime.of(20,0,0),member,room);
        List<String> hashtags = new ArrayList<>();
        hashtags.add(HashtagName.FOCUS.name());
        List<MultipartFile> multipartFiles = new ArrayList<>();

        doReturn(Optional.of(reservationRecord)).when(reservationRecordRepository).findById(reservationRecord.getId());

        RegisterReviewRequest request = RegisterReviewRequest.builder()
                .reservationId(reservationRecord.getId())
                .cleanliness(4)
                .deafening(4)
                .fixtureStatus(4)
                .isRecommend(true)
                .hashtags(hashtags)
                .detail("정말 최고의 스터디카페입니다. 다시 등록하고 싶어요")
                .build();

        // when
        RegisterReviewResponse response = reviewService.register(request, multipartFiles);

        // then
        Assertions.assertThat(reservationRecord.getReview()).isNotNull();
        Assertions.assertThat(response.getCreatedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("요청 DTO에 null값이 있는 경우 검증에 실패한다.")
    void 리뷰_등록_NULL_검증_실패() {
        // given
        Long reservationId = 1L;
        List<String> hashtags = new ArrayList<>();
        hashtags.add(HashtagName.FOCUS.name());

        RegisterReviewRequest request = RegisterReviewRequest.builder()
                .reservationId(reservationId)
                .cleanliness(null)
                .deafening(4)
                .fixtureStatus(4)
                .isRecommend(true)
                .hashtags(hashtags)
                .detail("정말 최고의 스터디카페입니다. 다시 등록하고 싶어요")
                .build();

        // when
        Set<ConstraintViolation<RegisterReviewRequest>> violations = validator.validate(request);

        // then
        Assertions.assertThat(violations.stream().anyMatch(
                error -> error.getMessage().equals("청결도 평점은 필수입니다.")
        )).isTrue();
    }

    @Test
    @DisplayName("등록한 리뷰를 수정할 수 있다.")
    void modifyReview() {
        // given
        Studycafe studycafe = NERDS.생성(1L);
        Review review = YESTERDAY_COMMENTED_REVIEW.평점_정보_생성(1L, 4,4,4,4.0);
        List<String> hashtags = new ArrayList<>();
        hashtags.add(HashtagName.FOCUS.name());
        List<MultipartFile> multipartFiles = new ArrayList<>();

        ModifyReviewRequest request = ModifyReviewRequest.builder()
                .cafeId(studycafe.getId())
                .cleanliness(4)
                .deafening(3)
                .fixtureStatus(4)
                .isRecommend(true)
                .hashtags(hashtags)
                .detail("정말 최고의 스터디카페입니다. 다시 등록하고 싶어요")
                .build();

        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
        doReturn(Optional.of(studycafe)).when(studycafeRepository).findById(studycafe.getId());

        // when
        ModifyReviewResponse modifyReviewResponse = reviewService.modifyReview(review.getId(), request, multipartFiles);
        Review modifiedReview = reviewRepository.findById(review.getId()).get();

        // then
        Assertions.assertThat(modifyReviewResponse.getReviewId()).isEqualTo(review.getId());
        Assertions.assertThat(modifiedReview.getGrade().getDeafening()).isEqualTo(3);
        Assertions.assertThat(modifiedReview.getDetail()).isEqualTo("정말 최고의 스터디카페입니다. 다시 등록하고 싶어요");
    }

    @Test
    @DisplayName("등록한 리뷰를 삭제할 수 있다.")
    void deleteReview() {
        // given
        Member member = DEFAULT_USER.생성();
        Studycafe studycafe = NERDS.생성();
        Room room = ROOM_TWO_FOUR.스터디카페_생성(studycafe);
        Review review = YESTERDAY_COMMENTED_REVIEW.평점_정보_생성(1L, 4,4,4,4.0);
        ReservationRecord reservationRecord = CONFIRM_RESERVATION.예약_내역_생성(
                LocalDate.of(2013,10,12),
                LocalTime.of(16,0,0),
                LocalTime.of(20,0,0),member,room);

        List<String> hashtags = new ArrayList<>();
        hashtags.add(HashtagName.FOCUS.name());

        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
        doReturn(Optional.of(reservationRecord)).when(reservationRecordRepository).findByReviewId(review.getId());

        // when
        reviewService.deleteReview(review.getId());

        // then
        verify(reviewRepository, atMostOnce()).deleteById(review.getId());
    }
}