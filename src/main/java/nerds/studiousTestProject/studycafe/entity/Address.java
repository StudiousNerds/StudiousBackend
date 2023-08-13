package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String basic;   // 기본 주소
    private String detail;  // 상세 주소
    private String zipcode; // 우편 번호

    public String getEntryAddress() {
        return basic + " " + detail;
    }

    @Builder
    public Address(String basic, String detail, String zipcode) {
        this.basic = basic;
        this.detail = detail;
        this.zipcode = zipcode;
    }
}
