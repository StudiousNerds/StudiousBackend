package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.studycafe.entity.Address;
import nerds.studiousTestProject.studycafe.entity.NearestStationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Studycafe.StudycafeBuilder;

import java.time.LocalDateTime;

import static nerds.studiousTestProject.support.fixture.AddressFixture.진접;
import static nerds.studiousTestProject.support.fixture.AddressFixture.창동;

public enum StudycafeFixture {

    NERDS("Nerds", "031-571-2378", "사진 경로",  진접.생성(), null, 2.3, 0, "공간 소개글", LocalDateTime.now().minusDays(12)),
    FIRST_STUDYCAFE("스프링카페", "031-571-2378", "사진 경로",  진접.생성(), null, 4.3, 0, "공간 소개글", LocalDateTime.now().minusDays(11)),
    SECOND_STUDYCAFE("스프카페", "031-571-2378", "사진 경로",  진접.생성(), null, 3.5, 0, "공간 소개글", LocalDateTime.now().minusDays(10)),
    THIRD_STUDYCAFE("스링카페", "031-571-2378", "사진 경로",  진접.생성(), null, 4.0, 0, "공간 소개글", LocalDateTime.now().minusDays(9)),
    FOURTH_STUDYCAFE("스프후카페", "031-571-2378", "사진 경로",  진접.생성(), null, 3.7, 0, "공간 소개글", LocalDateTime.now().minusDays(8)),
    FIFTH_STUDYCAFE("프링카페", "031-571-2378", "사진 경로",  진접.생성(), null, 4.5, 0, "공간 소개글", LocalDateTime.now().minusDays(7)),
    SIXTH_STUDYCAFE("링카페", "031-571-2378", "사진 경로",  창동.생성(), null, 5.0, 0, "공간 소개글", LocalDateTime.now().minusDays(6)),
    SEVENTH_STUDYCAFE("프카페", "031-571-2378", "사진 경로",  창동.생성(), null, 1.5, 0, "공간 소개글", LocalDateTime.now().minusDays(5)),
    EIGHTH_STUDYCAFE("스투카페", "031-571-2378", "사진 경로",  창동.생성(), null, 1.3, 0, "공간 소개글", LocalDateTime.now().minusDays(4)),
    NINETH_STUDYCAFE("프파카페", "031-571-2378", "사진 경로",  창동.생성(), null, 0.5, 0, "공간 소개글", LocalDateTime.now().minusDays(3)),
    TENTH_STUDYCAFE("링파카페", "031-571-2378", "사진 경로",  창동.생성(), null, 2.5, 0, "공간 소개글", LocalDateTime.now().minusDays(3)),
    ELEVENTH_STUDYCAFE("토비카페", "031-571-2378", "사진 경로",  창동.생성(), null, 2.0, 0, "공간 소개글", LocalDateTime.now().minusDays(1));

    private final String name;
    private final String tel;
    private final String photo;
    private final Address address;
    private final NearestStationInfo nearestStationInfo;
    private final Double totalGrade;
    private final Integer accumReserveCount;
    private final String introduction;
    private final LocalDateTime createdDate;

    public String getName() {
        return name;
    }

    StudycafeFixture(String name, String tel, String photo, Address address, NearestStationInfo nearestStationInfo, Double totalGrade, Integer accumReserveCount, String introduction, LocalDateTime createdDate) {
        this.name = name;
        this.tel = tel;
        this.photo = photo;
        this.address = address;
        this.nearestStationInfo = nearestStationInfo;
        this.totalGrade = totalGrade;
        this.accumReserveCount = accumReserveCount;
        this.introduction = introduction;
        this.createdDate = createdDate;
    }

    public Studycafe 생성(){
        return 생성(null);
    }

    public Studycafe 생성(Long id){
        return 기본_정보_빌더_생성(id).build();
    }

    public Studycafe 멤버_생성(Member member) {
        return 멤버_생성(member, null);
    }

    public Studycafe 멤버_생성(Member member, Long id) {
        return 기본_정보_빌더_생성(id).member(member).build();
    }

    public StudycafeBuilder 기본_정보_빌더_생성(Long id){
        return Studycafe.builder()
                .id(id)
                .name(this.name)
                .photo(this.photo)
                .address(this.address)
                .nearestStationInfo(this.nearestStationInfo)
                .totalGrade(this.totalGrade)
                .accumReserveCount(this.accumReserveCount)
                .tel(this.tel)
                .introduction(this.introduction)
                .createdDate(this.createdDate);
    }
}
