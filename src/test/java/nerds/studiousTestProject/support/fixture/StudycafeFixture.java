package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.studycafe.entity.Address;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Studycafe.StudycafeBuilder;

import java.time.LocalDateTime;

public enum StudycafeFixture {

    NERDS("Nerds", "031-571-2378", "사진 경로", null, null, 2.3, 0, "공간 소개글", LocalDateTime.now().minusDays(12)),
    FIRST_STUDYCAFE("스프링카페", "031-571-2378", "사진 경로", null, null, 4.3, 0, "공간 소개글", LocalDateTime.now().minusDays(11)),
    SECOND_STUDYCAFE("스프카페", "031-571-2378", "사진 경로", null, null, 3.5, 0, "공간 소개글", LocalDateTime.now().minusDays(10)),
    THIRD_STUDYCAFE("스링카페", "031-571-2378", "사진 경로",  null, null, 4.0, 0, "공간 소개글", LocalDateTime.now().minusDays(9)),
    FOURTH_STUDYCAFE("스프후카페", "031-571-2378", "사진 경로",  null, null, 3.7, 0, "공간 소개글", LocalDateTime.now().minusDays(8));

    private final String name;
    private final String tel;
    private final String photo;
    private final String nearestStation;
    private final Integer walkingTime;
    private final Double totalGrade;
    private final Integer accumReserveCount;
    private final String introduction;
    private final LocalDateTime createdDate;

    public String getName() {
        return name;
    }

    StudycafeFixture(String name, String tel, String photo, String nearestStation, Integer walkingTime, Double totalGrade, Integer accumReserveCount, String introduction, LocalDateTime createdDate) {
        this.name = name;
        this.tel = tel;
        this.photo = photo;
        this.nearestStation = nearestStation;
        this.walkingTime = walkingTime;
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

    public Studycafe 멤버_추가_생성(Member member) {
        return 멤버_추가_생성(member, null);
    }

    public Studycafe 멤버_추가_생성(Member member, Long id) {
        return 기본_정보_빌더_생성(id).member(member).build();
    }

    public StudycafeBuilder 기본_정보_빌더_생성(Long id){
        return Studycafe.builder()
                .id(id)
                .name(this.name)
                .photo(this.photo)
                .address(createAddress())
                .nearestStation(this.nearestStation)
                .walkingTime(this.walkingTime)
                .totalGrade(this.totalGrade)
                .accumReserveCount(this.accumReserveCount)
                .tel(this.tel)
                .introduction(this.introduction)
                .createdDate(this.createdDate);
    }

    private Address createAddress() {
        return Address.builder()
                .addressBasic("경기도 남양주시 진접읍 금강로 1530-14")
                .addressDetail("진접하우스토리 아파트 105동 1303호")
                .addressZipcode("12010")
                .build();
    }
}
