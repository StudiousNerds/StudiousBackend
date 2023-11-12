package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;

@Builder
@Data
public class ModifyConvenienceResponse {
    private ConvenienceName name;
    private Integer price;

    public static ModifyConvenienceResponse from(Convenience convenience) {
        return ModifyConvenienceResponse.builder()
                .name(convenience.getName())
                .price(convenience.getPrice())
                .build();
    }
}
