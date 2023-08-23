package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.studycafe.entity.Address;

public enum AddressFixture {
    창동("서울특별시 광진구 능등로 209", "세종대학교 학생회관 B01", "11111"),
    진접("경기도 남양주시 진접읍 금강로 1530-14", "진접하우스토리 아파트 105동 1303호", "12010");

    AddressFixture(String basic, String detail, String zipcode) {
        this.basic = basic;
        this.detail = detail;
        this.zipcode = zipcode;
    }

    private final String basic;
    private final String detail;
    private final String zipcode;

    public Address 생성() {
        return Address.builder()
                .addressBasic(basic)
                .addressDetail(detail)
                .addressZipcode(zipcode)
                .build();
    }
}
