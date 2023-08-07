package nerds.studiousTestProject.studycafe.dto.manage;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ManagedCafeInquireResponse {
    private Long id;
    private String name;
    private String address;
    private String photo;
}
