package nerds.studiousTestProject.member.dto.find.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindPasswordResponse {
    private String tempPassword;
}
