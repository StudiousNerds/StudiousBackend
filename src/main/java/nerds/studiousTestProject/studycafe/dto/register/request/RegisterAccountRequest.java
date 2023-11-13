package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterAccountRequest {
    @NotBlank(message = "은행명은 필수입니다.")
    private String name;

    @Size(min = 10, max = 14, message = "계좌 번호가 잘못되었습니다.")
    private String number;
}
