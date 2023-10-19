package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;

@Builder
@Data
public class ConvenienceInfoResponse {
    private ConvenienceName name;
    private Integer price;

    public static ConvenienceInfoResponse from(Convenience convenience) {
        return ConvenienceInfoResponse.builder()
                .name(convenience.getName())
                .price(convenience.getPrice())
                .build();
    }
}
