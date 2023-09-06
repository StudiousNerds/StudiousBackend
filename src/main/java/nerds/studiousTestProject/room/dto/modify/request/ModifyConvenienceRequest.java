package nerds.studiousTestProject.room.dto.modify.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.room.entity.Room;

@Builder
@Data
public class ModifyConvenienceRequest {
    private ConvenienceName name;

    @Min(value = 0, message = "가격은 0 이상입니다.")
    private Integer price;

    public Convenience toEntity() {
        return Convenience.builder()
                .name(name)
                .price(price)
                .isFree(price == 0)
                .build();
    }
}
