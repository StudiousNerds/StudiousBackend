package nerds.studiousTestProject.member.dto.general.find;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FindEmailRequest {
    @NotBlank(message = "전화번호는 공백일 수 없습니다.")
    private String phoneNumber;
}
