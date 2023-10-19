package nerds.studiousTestProject.member.dto.find.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindPasswordRequest {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호는 공백일 수 없습니다.")
    private String phoneNumber;
}
