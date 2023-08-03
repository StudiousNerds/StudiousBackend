package nerds.studiousTestProject.studycafe.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfo {
    @NotBlank(message = "은행명은 필수입니다.")
    private String name;        // 은행명 (추후, 열거체로 리펙토링)
    @Size(min = 10, max = 14, message = "계좌 번호가 잘못되었습니다.")   // 계좌 번호 길이는 10 ~ 14자
    private String number;      // 계좌 번호
}
