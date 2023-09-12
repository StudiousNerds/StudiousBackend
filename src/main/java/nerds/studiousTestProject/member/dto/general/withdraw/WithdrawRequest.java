package nerds.studiousTestProject.member.dto.general.withdraw;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WithdrawRequest {
    private String password;
}
