package nerds.studiousTestProject.member.dto.logout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutResponse {
    Long memberId;
}
