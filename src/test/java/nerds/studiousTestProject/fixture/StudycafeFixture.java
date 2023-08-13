package nerds.studiousTestProject.fixture;

import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Studycafe.StudycafeBuilder;

import java.time.LocalDateTime;

public enum StudycafeFixture {
    FIRST_STUDYCAFE("스프링카페", 4.3, LocalDateTime.now()),
    SECOND_STUDYCAFE("스프카페", 3.5, LocalDateTime.now().minusDays(1)),
    THIRD_STUDYCAFE("스링카페", 4.0, LocalDateTime.now().minusDays(10)),
    FOURTH_STUDYCAFE("스프후카페", 3.7, LocalDateTime.now().minusDays(5)),
    FIFTH_STUDYCAFE("프링카페", 4.5, LocalDateTime.now().minusDays(2)),
    SIXTH_STUDYCAFE("링카페", 5.0, LocalDateTime.now().minusDays(4)),
    SEVENTH_STUDYCAFE("프카페", 1.5, LocalDateTime.now().minusDays(11)),
    EIGHTH_STUDYCAFE("스투카페", 1.3, LocalDateTime.now().minusMonths(1)),
    NINETH_STUDYCAFE("프파카페", 0.5, LocalDateTime.now().minusDays(7)),
    TENTH_STUDYCAFE("링파카페", 2.5, LocalDateTime.now().minusDays(3)),
    ELEVENTH_STUDYCAFE("토비카페", 2.0, LocalDateTime.now().minusMonths(6));

    private final String name;
    private final Double totalGrade;
    private final LocalDateTime createdAt;

    StudycafeFixture(String name, Double totalGrade, LocalDateTime createdAt) {
        this.name = name;
        this.totalGrade = totalGrade;
        this.createdAt = createdAt;
    }

    public Studycafe 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    private StudycafeBuilder 기본_정보_생성() {
        return Studycafe.builder()
                .name(this.name);
    }
}
