package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressInfo {
    @NotBlank(message = "위도는 필수입니다.")
    private String latitude;    // 위도

    @NotBlank(message = "경도는 필수입니다.")
    private String longitude;   // 경도

    @NotBlank(message = "우편 번호는 필수입니다.")
    private String zipcode; // 우편 번호

    @NotBlank(message = "기본 주소는 필수입니다.")
    private String basic;   // 기본 주소

    @NotBlank(message = "상세 주소는 필수입니다.")
    private String detail;  // 상세 주소
}
