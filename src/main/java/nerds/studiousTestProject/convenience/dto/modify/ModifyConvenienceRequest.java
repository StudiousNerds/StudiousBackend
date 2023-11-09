package nerds.studiousTestProject.convenience.dto.modify;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;

@Builder
@Data
public class ModifyConvenienceRequest {
    private ConvenienceName name;

    @Min(value = 0, message = "가격은 0 이상입니다.")
    private Integer price;

    public Convenience toConvenience() {
        return Convenience.builder()
                .name(name)
                .price(price)
                .isFree(price == 0)
                .build();
    }
}
