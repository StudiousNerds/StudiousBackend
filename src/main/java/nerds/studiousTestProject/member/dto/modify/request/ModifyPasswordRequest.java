package nerds.studiousTestProject.member.dto.modify.request;

import lombok.Data;

@Data
public class ModifyPasswordRequest {
    private String oldPassword;
    private String newPassword;
}
