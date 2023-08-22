package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.request.SortType;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import nerds.studiousTestProject.support.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static nerds.studiousTestProject.convenience.entity.ConvenienceName.ELEVATOR;
import static nerds.studiousTestProject.convenience.entity.ConvenienceName.HDMI;
import static nerds.studiousTestProject.support.EntitySaveProvider.권한_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.룸_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.리뷰_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.스터디카페_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.예약_정보_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.운영_정보_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.편의시설_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.평점_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.해시테그_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.회원_저장;
import static nerds.studiousTestProject.support.fixture.AddressFixture.진접;
import static nerds.studiousTestProject.support.fixture.ConvenienceFixture.FIRST_CONVENIENCE;
import static nerds.studiousTestProject.support.fixture.ConvenienceFixture.SECOND_CONVENIENCE;
import static nerds.studiousTestProject.support.fixture.GradeFixture.FIRST_GRADE;
import static nerds.studiousTestProject.support.fixture.GradeFixture.SECOND_GRADE;
import static nerds.studiousTestProject.support.fixture.GradeFixture.THIRD_GRADE;
import static nerds.studiousTestProject.support.fixture.HashtagFixture.FIRST_HASHTAG;
import static nerds.studiousTestProject.support.fixture.HashtagFixture.SECOND_HASHTAG;
import static nerds.studiousTestProject.support.fixture.MemberFixture.BURNED_POTATO;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.FRI_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.HOL_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.MON_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.SAT_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.SUN_CLOSED;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.SUN_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.THU_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.TUE_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.OperationInfoFixture.WED_NINE_TO_NINE;
import static nerds.studiousTestProject.support.fixture.ReservationRecordFixture.CONFIRM_RESERVATION;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.FIRST_REVIEW;
import static nerds.studiousTestProject.support.fixture.RoleFixture.ADMIN;
import static nerds.studiousTestProject.support.fixture.RoleFixture.USER;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_FOUR_SIX;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_TWO_FOUR;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.FIRST_STUDYCAFE;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.FOURTH_STUDYCAFE;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.SECOND_STUDYCAFE;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.THIRD_STUDYCAFE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryTest
class StudycafeRepositoryCustomImplTest {

    @Autowired
    StudycafeRepository studycafeRepository;

    private Member admin;
    private Member reservation;

    /**
     * 스터디카페, 예약 내역 생성 시 사용되는 Member 엔티티를 미리 정의해둠
     */
    @BeforeEach
    public void init() {
        admin = 회원_저장(BURNED_POTATO.생성());
        권한_저장(ADMIN.멤버_생성(admin));

        reservation = 회원_저장(BURNED_POTATO.생성());
        권한_저장(USER.멤버_생성(reservation));
    }

    @Test
    @DisplayName("예약 인원 검색")
    public void 예약_인원_검색() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .headCount(3)
                .sortType(SortType.CREATED_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        Studycafe studycafe2 = 스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();
        System.out.println(responses.get(0).getRooms().get(0).getName());

        // then
        assertEquals(responses.size(), 1);
    }

    @Test
    @DisplayName("예약 날짜 및 시간 검색")
    public void 예약_날짜_및_시간_검색() throws Exception {

        // given
        LocalDate reservedDate = LocalDate.of(2023, 7, 30);
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(14, 0);

        SearchRequest request = SearchRequest.builder()
                .date(reservedDate)
                .startTime(startTime)
                .endTime(endTime)
                .sortType(SortType.CREATED_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin));
        운영_정보_저장(MON_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(TUE_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(WED_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(THU_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(FRI_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(SAT_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(SUN_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(HOL_NINE_TO_NINE.스터디카페_생성(studycafe1));

        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(reservedDate, startTime, endTime, reservation, 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(reservedDate, startTime, endTime, reservation, 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(reservedDate, startTime, endTime, reservation, 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1))));

        Studycafe studycafe2 = 스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin));
        운영_정보_저장(MON_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(TUE_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(WED_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(THU_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(FRI_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(SAT_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(SUN_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(HOL_NINE_TO_NINE.스터디카페_생성(studycafe2));

        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 1);  // 예약 내역이 있는 한 곳만 제외
    }

    @Test
    @DisplayName("운영하지 않는 날짜 및 시간에 검색")
    public void 운영X_날짜_및_시간_검색() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .date(LocalDate.of(2023, 7, 30))
                .sortType(SortType.CREATED_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin));
        운영_정보_저장(MON_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(TUE_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(WED_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(THU_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(FRI_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(SAT_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(SUN_NINE_TO_NINE.스터디카페_생성(studycafe1));
        운영_정보_저장(HOL_NINE_TO_NINE.스터디카페_생성(studycafe1));

        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        Studycafe studycafe2 = 스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin));
        운영_정보_저장(MON_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(TUE_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(WED_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(THU_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(FRI_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(SAT_NINE_TO_NINE.스터디카페_생성(studycafe2));
        운영_정보_저장(SUN_CLOSED.스터디카페_생성(studycafe2));
        운영_정보_저장(HOL_NINE_TO_NINE.스터디카페_생성(studycafe2));

        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();

        // then
        System.out.println(responses);
        assertEquals(responses.size(), 1);
    }

    @Test
    @DisplayName("키워드 검색")
    public void 키워드_검색() throws Exception {

        // given
        SearchRequest nameSearchRequest = SearchRequest.builder()
                .keyword("Nerd")
                .sortType(SortType.CREATED_DESC)
                .build();

        SearchRequest addressSearchRequest = SearchRequest.builder()
                .keyword("Nerd")
                .sortType(SortType.CREATED_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        Studycafe studycafe2 = 스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));
        룸_저장(ROOM_TWO_FOUR.스터디카페_생성(studycafe2));

        List<Studycafe> nameSearchResponses = studycafeRepository.getSearchResult(nameSearchRequest, pageable()).getContent();
        List<Studycafe> addressSearchResponses = studycafeRepository.getSearchResult(addressSearchRequest, pageable()).getContent();

        // then
        assertEquals(nameSearchResponses.get(0).getName(), "Nerds");
        assertTrue(addressSearchResponses.get(0).getAddress().getAddressBasic().contains("진접"));
    }

    @Test
    @DisplayName("평점 필터링")
    public void 평점_필터링() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .minGrade(2)
                .sortType(SortType.CREATED_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        Review review1 = 리뷰_저장(FIRST_REVIEW.생성(null));
        평점_저장(FIRST_GRADE.리뷰_생성(review1, null));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, review1));

        Studycafe studycafe2 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));

        Review review2 = 리뷰_저장(FIRST_REVIEW.생성(null));
        평점_저장(THIRD_GRADE.리뷰_생성(review2, null));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2));

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 1);
    }

    @Test
    @DisplayName("해시 태그 필터링")
    public void 해시_테그_필터링() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .hashtags(List.of(HashtagName.COST_EFFECTIVE))
                .sortType(SortType.CREATED_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        Review review1 = 리뷰_저장(FIRST_REVIEW.생성(null));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, review1));
        해시테그_저장(FIRST_HASHTAG.리뷰_생성(review1));
        평점_저장(FIRST_GRADE.리뷰_생성(review1, null));

        Studycafe studycafe2 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));
        Review review2 = 리뷰_저장(FIRST_REVIEW.생성(null));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2));
        해시테그_저장(SECOND_HASHTAG.리뷰_생성(review2));
        평점_저장(THIRD_GRADE.리뷰_생성(review2, null));

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 1);
        assertEquals(responses.get(0).getRooms().get(0).getReservationRecords().get(0).getReview().getHashtagRecords().get(0).getName().name(), "COST_EFFECTIVE");
    }

    @Test
    @DisplayName("편의시설 필터링")
    public void 편의시설_필터링() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .conveniences(List.of(ELEVATOR, HDMI))
                .sortType(SortType.CREATED_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        편의시설_저장(FIRST_CONVENIENCE.스터디카페_생성(studycafe1, null));
        편의시설_저장(SECOND_CONVENIENCE.룸_생성(room1, null));

        룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(NERDS.멤버_생성(admin))));

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 1);
    }

    @Test
    @DisplayName("예약 많은 순 정렬")
    public void 예약_많은_순_정렬() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .sortType(SortType.RESERVATION_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));
        for (int i = 1; i <= 40; i++) {
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1));
        }

        Studycafe studycafe2 = 스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));
        for (int i = 1; i <= 30; i++) {
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2));
        }

        Studycafe studycafe3 = 스터디카페_저장(THIRD_STUDYCAFE.멤버_생성(admin));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe3));
        for (int i = 1; i <= 20; i++) {
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room3));
        }

        Studycafe studycafe4 = 스터디카페_저장(FOURTH_STUDYCAFE.멤버_생성(admin));
        Room room4 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe4));
        for (int i = 1; i <= 10; i++) {
            예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room4));
        }

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 4);
        assertEquals(responses.get(0).getName(), FIRST_STUDYCAFE.getName());
        assertEquals(responses.get(1).getName(), SECOND_STUDYCAFE.getName());
        assertEquals(responses.get(2).getName(), THIRD_STUDYCAFE.getName());
        assertEquals(responses.get(3).getName(), FOURTH_STUDYCAFE.getName());
    }

    @Test
    @DisplayName("평점 높은 순 정렬")
    public void 평점_높은_순_정렬() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .sortType(SortType.GRADE_DESC)
                .build();

        // when
        Studycafe studycafe1 = 스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe1));

        Review review1 = 리뷰_저장(FIRST_REVIEW.생성(null));
        평점_저장(FIRST_GRADE.리뷰_생성(review1, null));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, review1));

        Studycafe studycafe2 = 스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe2));

        Review review2 = 리뷰_저장(FIRST_REVIEW.생성(null));
        평점_저장(SECOND_GRADE.리뷰_생성(review2, null));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2, review2));

        Studycafe studycafe3 = 스터디카페_저장(THIRD_STUDYCAFE.멤버_생성(admin));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe3));

        Review review3 = 리뷰_저장(FIRST_REVIEW.생성(null));
        평점_저장(THIRD_GRADE.리뷰_생성(review3, null));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room3, review3));

        List<Studycafe> responses = studycafeRepository.getSearchResult(request, pageable()).getContent();
        System.out.println(responses.get(0).getRooms().get(0).getReservationRecords().get(0).getReview().getGrade().getTotal());

        // then
        assertEquals(responses.size(), 3);
        assertEquals(responses.get(0).getName(), FIRST_STUDYCAFE.getName());
        assertEquals(responses.get(1).getName(), SECOND_STUDYCAFE.getName());
        assertEquals(responses.get(2).getName(), THIRD_STUDYCAFE.getName());
    }

    @Test
    @DisplayName("리뷰 많은 순 정렬")
    public void 리뷰_많은_순_정렬() throws Exception {

        // given
        SearchRequest searchRequest = SearchRequest.builder()
                .sortType(SortType.REVIEW_DESC)
                .build();

        // when
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, 리뷰_저장(FIRST_REVIEW.생성(null))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, 리뷰_저장(FIRST_REVIEW.생성(null))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, 리뷰_저장(FIRST_REVIEW.생성(null))));

        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2, 리뷰_저장(FIRST_REVIEW.생성(null))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2, 리뷰_저장(FIRST_REVIEW.생성(null))));

        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(THIRD_STUDYCAFE.멤버_생성(admin))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room3, 리뷰_저장(FIRST_REVIEW.생성(null))));

        List<Studycafe> responses = studycafeRepository.getSearchResult(searchRequest, pageable()).getContent();

        // then
        assertEquals(responses.size(), 3);
        assertEquals(responses.get(0).getName(), FIRST_STUDYCAFE.getName());
        assertEquals(responses.get(1).getName(), SECOND_STUDYCAFE.getName());
        assertEquals(responses.get(2).getName(), THIRD_STUDYCAFE.getName());
    }

    @Test
    @DisplayName("리뷰 적은 순 정렬")
    public void 리뷰_적은_순_정렬() throws Exception {

        // given
        SearchRequest searchRequest = SearchRequest.builder()
                .sortType(SortType.REVIEW_ASC)
                .build();

        // when
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, 리뷰_저장(FIRST_REVIEW.생성(null))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, 리뷰_저장(FIRST_REVIEW.생성(null))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room1, 리뷰_저장(FIRST_REVIEW.생성(null))));

        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2, 리뷰_저장(FIRST_REVIEW.생성(null))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room2, 리뷰_저장(FIRST_REVIEW.생성(null))));

        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(스터디카페_저장(THIRD_STUDYCAFE.멤버_생성(admin))));
        예약_정보_저장(CONFIRM_RESERVATION.예약_내역_생성(LocalDate.now(), LocalTime.MIN, LocalTime.MAX, reservation, room3, 리뷰_저장(FIRST_REVIEW.생성(null))));

        List<Studycafe> responses = studycafeRepository.getSearchResult(searchRequest, pageable()).getContent();

        // then
        assertEquals(responses.size(), 3);
        assertEquals(responses.get(2).getName(), FIRST_STUDYCAFE.getName());
        assertEquals(responses.get(1).getName(), SECOND_STUDYCAFE.getName());
        assertEquals(responses.get(0).getName(), THIRD_STUDYCAFE.getName());
    }

    private Pageable pageable() {
        return PageRequestConverter.of(0, 8);
    }
}