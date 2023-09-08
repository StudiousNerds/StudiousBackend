package nerds.studiousTestProject.member.dto.patch;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatchNicknameRequest {
    private String newNickname;
}
