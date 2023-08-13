package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountInfoRequest {
    @NotBlank(message = "은행명은 필수입니다.")
    private String name;        // 은행명 (추후, 열거체로 리펙토링)

    @Size(min = 10, max = 14, message = "계좌 번호가 잘못되었습니다.")   // 계좌 번호 길이는 10 ~ 14자
    private String number;      // 계좌 번호
}
