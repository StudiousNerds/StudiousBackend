package nerds.studiousTestProject.member.dto.general.patch;

import lombok.Data;

@Data
public class PatchPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
