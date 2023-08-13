package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Studycafe.StudycafeBuilder;

public enum StudycafeFixture {

    NERDS("Nerds");

    private final String name;

    StudycafeFixture(String name) {
        this.name = name;
    }

    public Studycafe 생성(){
        return 생성(null);
    }
    public Studycafe 생성(Long id){
        return 기본_정보_빌더_생성(id).build();
    }
    public StudycafeBuilder 기본_정보_빌더_생성(Long id){
        return Studycafe.builder()
                .id(id)
                .name(this.name);
    }
}
