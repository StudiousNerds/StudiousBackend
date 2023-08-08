package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;

@Builder
@Data
public class ConvenienceInfoResponse {
    private ConvenienceName name;
    private Integer price;
}
