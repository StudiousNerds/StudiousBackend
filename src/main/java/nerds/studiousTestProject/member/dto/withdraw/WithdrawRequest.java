package nerds.studiousTestProject.member.dto.withdraw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WithdrawRequest {
    private String password;
}
