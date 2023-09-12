package nerds.studiousTestProject.member.dto.patch;

import lombok.Data;

@Data
public class PatchPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
