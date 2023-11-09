package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Address;

@Data
public class RegisterAddressRequest {
    @NotBlank(message = "위도는 필수입니다.")
    private String latitude;

    @NotBlank(message = "경도는 필수입니다.")
    private String longitude;

    @NotBlank(message = "우편 번호는 필수입니다.")
    private String zipcode;

    @NotBlank(message = "기본 주소는 필수입니다.")
    private String basic;

    @NotBlank(message = "상세 주소는 필수입니다.")
    private String detail;

    public Address toAddress() {
        return Address.builder()
                .addressBasic(basic)
                .addressDetail(detail)
                .addressZipcode(zipcode)
                .build();
    }
}
