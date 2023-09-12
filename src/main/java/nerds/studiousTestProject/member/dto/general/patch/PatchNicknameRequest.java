package nerds.studiousTestProject.member.dto.general.patch;

import lombok.Builder;
import lombok.Data;

@Data
public class PatchNicknameRequest {
    private String newNickname;
}
