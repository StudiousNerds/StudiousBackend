package nerds.studiousTestProject.studycafe.repository;

import jakarta.persistence.EntityManager;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.sql.Time;
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
        Studycafe studycafe1 = studycafe1();    // 예약 내역 O
        Room room1 = room1(studycafe1);
        Room room2 = room1(studycafe1);
        Room room3 = room1(studycafe1);
        ReservationRecord reservationRecord1 = reservationRecord1(room1);
        ReservationRecord reservationRecord2 = reservationRecord1(room2);
        ReservationRecord reservationRecord3 = reservationRecord1(room3);

        Studycafe studycafe2 = studycafe2();    // 예약 내역 O
        Room room4 = room2(studycafe2);
        Room room5 = room2(studycafe2);
        Room room6 = room2(studycafe2);
        ReservationRecord reservationRecord4 = reservationRecord2(room4);
        ReservationRecord reservationRecord5 = reservationRecord2(room5);
        ReservationRecord reservationRecord6 = reservationRecord2(room6);

        Studycafe studycafe3 = studycafe3();    // 예약 내역 X
        Room room7 = room1(studycafe3);
        Room room8 = room1(studycafe3);
        Room room9 = room1(studycafe3);

        Studycafe studycafe4 = studycafe4();    // 예약 내역 X
        Room room10 = room2(studycafe4);
        Room room11 = room2(studycafe4);
        Room room12 = room2(studycafe4);

        em.persist(studycafe1);
        em.persist(studycafe2);
        em.persist(studycafe3);
        em.persist(studycafe4);
        em.persist(room1);
        em.persist(room2);
        em.persist(room3);
        em.persist(room4);
        em.persist(room5);
        em.persist(room6);
        em.persist(room7);
        em.persist(room8);
        em.persist(room9);
        em.persist(room10);
        em.persist(room11);
        em.persist(room12);
        em.persist(reservationRecord1);
        em.persist(reservationRecord2);
        em.persist(reservationRecord3);
        em.persist(reservationRecord4);
        em.persist(reservationRecord5);
        em.persist(reservationRecord6);

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
                .date(Date.valueOf("2023-07-30"))
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
                .date(Date.valueOf("2023-07-30"))
                .startTime(Time.valueOf("13:00:00"))
                .endTime(Time.valueOf("14:00:00"))
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        System.out.println(responses);
        assertEquals(responses.size(), 3);  // 예약 내역이 있는 한 곳만 제외
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
    @DisplayName("평점 범위 필터링")
    public void 평점_범위_필터링() throws Exception {

        // given
        SearchRequest request = SearchRequest.builder()
                .minGrade(2)
                .maxGrade(4)
                .build();

        // when
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request, pageable()).getContent();

        // then
        assertEquals(responses.size(), 2);
    }

    private Studycafe studycafe1() {
        return Studycafe.builder()
                .name("테스트1 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(100)
                .startTime(Time.valueOf("09:00:00"))
                .endTime(Time.valueOf("21:00:00"))
                .totalGrade(1.2)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .notice("유의 사항")
                .build();
    }

    private Studycafe studycafe2() {
        return Studycafe.builder()
                .name("테스트2 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(100)
                .startTime(Time.valueOf("09:00:00"))
                .endTime(Time.valueOf("21:00:00"))
                .totalGrade(2.4)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .notice("유의 사항")
                .build();
    }

    private Studycafe studycafe3() {
        return Studycafe.builder()
                .name("테스트3 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(100)
                .startTime(Time.valueOf("09:00:00"))
                .endTime(Time.valueOf("21:00:00"))
                .totalGrade(3.6)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .notice("유의 사항")
                .build();
    }

    private Studycafe studycafe4() {
        return Studycafe.builder()
                .name("테스트4 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(100)
                .startTime(Time.valueOf("09:00:00"))
                .endTime(Time.valueOf("21:00:00"))
                .totalGrade(4.8)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .notice("유의 사항")
                .build();
    }

    private Room room1(Studycafe studycafe) {
        return Room.builder()
                .studycafe(studycafe)
                .headCount(4)
                .minHeadCount(2)
                .maxHeadCount(4)
                .minUsingTime(1)
                .price(3000)
                .build();
    }

    private Room room2(Studycafe studycafe) {
        return Room.builder()
                .studycafe(studycafe)
                .headCount(10)
                .minHeadCount(8)
                .maxHeadCount(10)
                .minUsingTime(1)
                .price(3000)
                .build();
    }

    private ReservationRecord reservationRecord1(Room room) {
        return ReservationRecord.builder()
                .room(room)
                .date(Date.valueOf("2023-07-30"))
                .startTime(Time.valueOf("13:00:00"))
                .endTime(Time.valueOf("15:00:00"))
                .duration(2)
                .headCount(5)
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    private ReservationRecord reservationRecord2(Room room) {
        return ReservationRecord.builder()
                .room(room)
                .date(Date.valueOf("2023-07-30"))
                .startTime(Time.valueOf("09:00:00"))
                .endTime(Time.valueOf("11:00:00"))
                .duration(2)
                .headCount(5)
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    private Pageable pageable() {
        return PageRequestConverter.of(0);
    }
}