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
    private String addressBasic;   // 기본 주소
    private String addressDetail;  // 상세 주소
    private String addressZipcode; // 우편 번호

    public String getEntryAddress() {
        return addressBasic + " " + addressDetail;
    }

    @Builder
    public Address(String addressBasic, String addressDetail, String addressZipcode) {
        this.addressBasic = addressBasic;
        this.addressDetail = addressDetail;
        this.addressZipcode = addressZipcode;
    }
}
