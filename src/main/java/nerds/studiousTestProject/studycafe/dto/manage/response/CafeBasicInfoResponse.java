package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CafeBasicInfoResponse {
    private Long id;
    private String name;
    private String address;
    private String photo;
}
