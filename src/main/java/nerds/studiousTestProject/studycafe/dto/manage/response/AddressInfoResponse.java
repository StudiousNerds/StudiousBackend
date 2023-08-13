package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Address;

@Builder
@Data
public class AddressInfoResponse {
    private String basic;   // 기본 주소
    private String detail;  // 상세 주소
    private String zipcode; // 우편 번호

    public static AddressInfoResponse from(Address address) {
        return AddressInfoResponse.builder()
                .basic(address.getBasic())
                .detail(address.getDetail())
                .zipcode(address.getZipcode())
                .build();
    }
}
