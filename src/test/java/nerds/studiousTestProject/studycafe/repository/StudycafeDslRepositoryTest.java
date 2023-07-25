package nerds.studiousTestProject.studycafe.repository;

import jakarta.persistence.EntityManager;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        Studycafe studycafe1 = studycafe1();
        Room room1 = room1(studycafe1);
        Room room2 = room1(studycafe1);
        Room room3 = room1(studycafe1);

        Studycafe studycafe2 = studycafe2();
        Room room4 = room2(studycafe2);
        Room room5 = room2(studycafe2);
        Room room6 = room2(studycafe2);

        em.persist(studycafe1);
        em.persist(studycafe2);
        em.persist(room1);
        em.persist(room2);
        em.persist(room3);
        em.persist(room4);
        em.persist(room5);
        em.persist(room6);
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
        List<SearchResponse> responses = studycafeDslRepository.searchAll(request);
        System.out.println(responses);

        // then
        assertEquals(responses.size(), 1);
    }

    private Studycafe studycafe1() {
        return Studycafe.builder()
                .name("진접 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(10)
                .startTime(Time.valueOf("09:00:00"))
                .endTime(Time.valueOf("21:00:00"))
                .totalGrade(3.1)
                .duration(null)
                .nearestStation(null)
                .introduction("소개글")
                .notificationInfo("공지 사항")
                .notice("유의 사항")
                .build();
    }

    private Studycafe studycafe2() {
        return Studycafe.builder()
                .name("진접 스터디카페")
                .address("경기도 남양주시 진접읍")
                .accumReserveCount(10)
                .startTime(Time.valueOf("09:00:00"))
                .endTime(Time.valueOf("21:00:00"))
                .totalGrade(5.0)
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
}