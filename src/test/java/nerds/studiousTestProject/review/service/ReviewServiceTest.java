package nerds.studiousTestProject.review.service;

import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.repository.AccumHashtagHistoryRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.dto.register.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.repository.GradeRepository;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
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

import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CONFIRM_RESERVATION;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_TWO_FOUR;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static org.mockito.Mockito.doReturn;

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
}