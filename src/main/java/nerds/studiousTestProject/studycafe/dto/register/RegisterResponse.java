package nerds.studiousTestProject.studycafe.dto.register;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterResponse {
    private String cafeName;
}
