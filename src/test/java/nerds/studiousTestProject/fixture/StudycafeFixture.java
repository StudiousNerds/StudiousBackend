package nerds.studiousTestProject.fixture;

import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Studycafe.StudycafeBuilder;

public enum StudycafeFixture {
    FIRST_STUDYCAFE("스프링카페");

    private final String name;

    StudycafeFixture(String name) {
        this.name = name;
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
