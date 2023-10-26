package nerds.studiousTestProject.member.dto.modify.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ModifyNicknameResponse {
    private String modifiedNickname;
}
