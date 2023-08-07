package nerds.studiousTestProject.studycafe.repository;

import jakarta.persistence.EntityManager;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.request.SortType;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.entity.Notice;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Week;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QueryDslRepositoryTest
class StudycafeDslRepositoryTest {

    @Autowired
    StudycafeDslRepository studycafeDslRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    public void beforeEach() {
        HashtagRecord hashtag1 = hashtag1();
        HashtagRecord hashtag2 = hashtag2();
        List<HashtagRecord> hashtagRecords1 = List.of(hashtag1, hashtag2);

        Convenience hdmi = hdmi();
        Convenience park = park();
        List<Convenience> roomConveniences1 = List.of(hdmi);
        List<Convenience> cafeConveniences1 = List.of(park);

        Studycafe studycafe1 = studycafe1(hashtagRecords1, cafeConveniences1);    // 예약 내역 O
        Room room1 = room1(roomConveniences1);
        Room room2 = room1(roomConveniences1);
        Room room3 = room1(roomConveniences1);
        studycafe1.addRoom(room1);
        studycafe1.addRoom(room2);
        studycafe1.addRoom(room3);
        Notice notice1 = notices();
        studycafe1.addNotice(notice1);

        ReservationRecord reservationRecord1 = reservationRecord_13_15(room1);
        room1.addReservationRecord(reservationRecord1);
        ReservationRecord reservationRecord2 = reservationRecord_13_15(room2);
        room2.addReservationRecord(reservationRecord2);
        ReservationRecord reservationRecord3 = reservationRecord_13_15(room3);
        room3.addReservationRecord(reservationRecord3);

        HashtagRecord hashtag3 = hashtag3();
        HashtagRecord hashtag4 = hashtag4();
        List<HashtagRecord> hashtagRecords2 = List.of(hashtag3, hashtag4);

        Studycafe studycafe2 = studycafe2(hashtagRecords2);    // 예약 내역 O
        Room room4 = room2();
        Room room5 = room2();
        Room room6 = room2();
        studycafe2.addRoom(room4);
        studycafe2.addRoom(room5);
        studycafe2.addRoom(room6);
        Notice notice2 = notices();
        studycafe2.addNotice(notice2);

        ReservationRecord reservationRecord4 = reservationRecord_09_11(room4);
        room4.addReservationRecord(reservationRecord4);
        ReservationRecord reservationRecord5 = reservationRecord_09_11(room5);
        room5.addReservationRecord(reservationRecord5);
        ReservationRecord reservationRecord6 = reservationRecord_09_11(room6);
        room6.addReservationRecord(reservationRecord6);

        Convenience beam = beam();
        Convenience elevator = elevator();
        List<Convenience> roomConveniences2 = List.of(beam);
        List<Convenience> cafeConveniences2 = List.of(elevator);

        Studycafe studycafe3 = studycafe3(cafeConveniences2);    // 예약 내역 X
        Room room7 = room1(roomConveniences2);
        Room room8 = room1(roomConveniences2);
        Room room9 = room1(roomConveniences2);
        studycafe3.addRoom(room7);
        studycafe3.addRoom(room8);
        studycafe3.addRoom(room9);
        Notice notice3 = notices();
        studycafe3.addNotice(notice3);

        Studycafe studycafe4 = studycafe4();    // 예약 내역 X
        Room room10 = room2();
        Room room11 = room2();
        Room room12 = room2();
        studycafe4.addRoom(room10);
        studycafe4.addRoom(room11);
        studycafe4.addRoom(room12);
        Notice notice4 = notices();
        studycafe4.addNotice(notice4);

        em.persist(studycafe1);
        em.persist(studycafe2);
        em.persist(studycafe3);
        em.persist(studycafe4);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("예약 인원 검색")
    public void 예약_인원_검색() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .headCount(3)
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 2);
    }

    @Test
    @DisplayName("예약 날짜만으로 검색")
    public void 예약_날짜_검색_시간X() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .date(LocalDate.of(2023, 7, 30))
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        System.out.println(responses);
        assertEquals(responses.size(), 4);
    }

    @Test
    @DisplayName("예약 날짜 및 시간 검색")
    public void 예약_날짜_및_시간_검색() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .date(LocalDate.of(2023, 7, 30))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(14, 0))
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        System.out.println(responses);
        assertEquals(responses.size(), 3);  // 예약 내역이 있는 한 곳만 제외
    }

    @Test
    @DisplayName("운영하지 않는 날짜 및 시간에 검색")
    public void 운영X_날짜_및_시간_검색() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .date(LocalDate.of(2023, 7, 29))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(14, 0))
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        System.out.println(responses);
        assertEquals(responses.size(), 0);  // 예약 내역이 있는 한 곳만 제외
    }

    @Test
    @DisplayName("키워드 검색")
    public void 키워드_검색() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .keyword("테스트")
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 4);
    }

    @Test
    @DisplayName("평점 필터링")
    public void 평점_필터링() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .minGrade(2)
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 3);
    }

    @Test
    @DisplayName("해시 태그 필터링")
    public void 해시_테그_필터링() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .hashtags(hashtagNames())
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 1);
    }

    @Test
    @DisplayName("편의시설 필터링")
    public void 편의시설_필터링() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .conveniences(convenienceNames())
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

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
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        Assertions.assertThat(responses.get(0).getAccumRevCnt()).isEqualTo(40);
        Assertions.assertThat(responses.get(1).getAccumRevCnt()).isEqualTo(30);
        Assertions.assertThat(responses.get(2).getAccumRevCnt()).isEqualTo(20);
        Assertions.assertThat(responses.get(3).getAccumRevCnt()).isEqualTo(10);
    }

    @Test
    @DisplayName("평점 높은 순 정렬")
    public void 평점_높은_순_정렬() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .sortType(SortType.GRADE_DESC)
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        Assertions.assertThat(responses.get(0).getGrade()).isEqualTo(4.8);
        Assertions.assertThat(responses.get(1).getGrade()).isEqualTo(3.6);
        Assertions.assertThat(responses.get(2).getGrade()).isEqualTo(2.4);
        Assertions.assertThat(responses.get(3).getGrade()).isEqualTo(1.2);
    }

    private Studycafe studycafe1(List<HashtagRecord> hashtagRecords, List<Convenience> conveniences) {
        Studycafe studycafe = Studycafe.builder()
                .name("테스트1 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(40)
                .totalGrade(1.2)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .build();

        List<OperationInfo> operationInfos = operationInfos();
        for (OperationInfo operationInfo : operationInfos) {
            studycafe.addOperationInfo(operationInfo);
        }

        for (HashtagRecord hashtagRecord : hashtagRecords) {
            studycafe.addHashtagRecord(hashtagRecord);
        }

        for (Convenience convenience : conveniences) {
            studycafe.addConvenience(convenience);
        }

        return studycafe;
    }

    private Studycafe studycafe2(List<HashtagRecord> hashtagRecords) {
        Studycafe studycafe = Studycafe.builder()
                .name("테스트2 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(30)
                .totalGrade(2.4)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .build();

        List<OperationInfo> operationInfos = operationInfos();
        for (OperationInfo operationInfo : operationInfos) {
            studycafe.addOperationInfo(operationInfo);
        }

        for (HashtagRecord hashtagRecord : hashtagRecords) {
            studycafe.addHashtagRecord(hashtagRecord);
        }

        return studycafe;
    }

    private Studycafe studycafe3(List<Convenience> conveniences) {
        Studycafe studycafe = Studycafe.builder()
                .name("테스트3 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(20)
                .totalGrade(3.6)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .build();

        List<OperationInfo> operationInfos = operationInfos();
        for (OperationInfo operationInfo : operationInfos) {
            studycafe.addOperationInfo(operationInfo);
        }

        for (Convenience convenience : conveniences) {
            studycafe.addConvenience(convenience);
        }

        return studycafe;
    }

    private Studycafe studycafe4() {
        Studycafe studycafe = Studycafe.builder()
                .name("테스트4 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(10)
                .totalGrade(4.8)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .build();

        List<OperationInfo> operationInfos = operationInfos();
        for (OperationInfo operationInfo : operationInfos) {
            studycafe.addOperationInfo(operationInfo);
        }

        return studycafe;
    }

    private List<OperationInfo> operationInfos() {
        return List.of(
                OperationInfo.builder()
                        .week(Week.MONDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(false)
                        .build(),
                OperationInfo.builder()
                        .week(Week.TUESDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(false)
                        .build(),
                OperationInfo.builder()
                        .week(Week.WEDNESDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(false)
                        .build(),
                OperationInfo.builder()
                        .week(Week.THURSDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(false)
                        .build(),
                OperationInfo.builder()
                        .week(Week.FRIDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(false)
                        .build(),
                OperationInfo.builder()
                        .week(Week.SATURDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(true)   // 2023-07-29 에는 조회 안되도록 반영
                        .build(),
                OperationInfo.builder()
                        .week(Week.SUNDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(false)
                        .build(),
                OperationInfo.builder()
                        .week(Week.HOLIDAY)
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(21, 0))
                        .allDay(false)
                        .closed(false)
                        .build()
        );
    }

    private Room room1(List<Convenience> conveniences) {
        Room room = Room.builder()
                .standardHeadCount(4)
                .minHeadCount(2)
                .maxHeadCount(4)
                .minUsingTime(1)
                .price(3000)
                .build();

        for (Convenience convenience : conveniences) {
            room.addConvenience(convenience);
        }

        return room;
    }

    private Room room2() {
        return Room.builder()
                .standardHeadCount(10)
                .minHeadCount(8)
                .maxHeadCount(10)
                .minUsingTime(1)
                .price(3000)
                .build();
    }

    private ReservationRecord reservationRecord_13_15(Room room) {
        return ReservationRecord.builder()
                .room(room)
                .date(LocalDate.of(2023, 7, 30))
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(15, 0))
                .duration(2)
                .headCount(5)
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    private ReservationRecord reservationRecord_09_11(Room room) {
        return ReservationRecord.builder()
                .room(room)
                .date(LocalDate.of(2023, 7, 30))
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .duration(2)
                .headCount(5)
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    private Notice notices() {
        return Notice.builder()
                .detail("유의 사항")
                .build();
    }

    private List<HashtagName> hashtagNames() {
        List<HashtagName> hashtagNames = new ArrayList<>();
        hashtagNames.add(HashtagName.STATION_AREA);
        hashtagNames.add(HashtagName.CLEAN);

        return hashtagNames;
    }

    private HashtagRecord hashtag1() {
        return HashtagRecord.builder()
                .name(HashtagName.STATION_AREA)
                .count(20)
                .build();
    }

    private HashtagRecord hashtag2() {
        return HashtagRecord.builder()
                .name(HashtagName.CLEAN)
                .count(10)
                .build();
    }

    private HashtagRecord hashtag3() {
        return HashtagRecord.builder()
                .name(HashtagName.COST_EFFECTIVE)
                .count(20)
                .build();
    }

    private HashtagRecord hashtag4() {
        return HashtagRecord.builder()
                .name(HashtagName.ACCESS)
                .count(10)
                .build();
    }

    private List<ConvenienceName> convenienceNames() {
        List<ConvenienceName> convenienceNames = new ArrayList<>();
        convenienceNames.add(ConvenienceName.HDMI);
        convenienceNames.add(ConvenienceName.PARKING);

        return convenienceNames;
    }

    private Convenience hdmi() {
        return Convenience.builder()
                .name(ConvenienceName.HDMI)
                .price(0)
                .build();
    }

    private Convenience park() {
        return Convenience.builder()
                .name(ConvenienceName.PARKING)
                .price(0)
                .build();
    }

    private Convenience beam() {
        return Convenience.builder()
                .name(ConvenienceName.BEAM)
                .price(0)
                .build();
    }

    private Convenience elevator() {
        return Convenience.builder()
                .name(ConvenienceName.ELEVATOR)
                .price(0)
                .build();
    }


    private Pageable pageable() {
        return PageRequestConverter.of(0, 8);
    }
}