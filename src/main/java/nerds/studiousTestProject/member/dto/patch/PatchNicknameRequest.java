package nerds.studiousTestProject.member.dto.patch;

import lombok.Builder;
import lombok.Data;

@Data
public class PatchNicknameRequest {
    private String newNickname;
}
