package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Address;

@Builder
@Data
public class ModifyAddressResponse {
    private String basic;   // 기본 주소
    private String detail;  // 상세 주소
    private String zipcode; // 우편 번호

    public static ModifyAddressResponse from(Address address) {
        return ModifyAddressResponse.builder()
                .basic(address.getAddressBasic())
                .detail(address.getAddressDetail())
                .zipcode(address.getAddressZipcode())
                .build();
    }
}
