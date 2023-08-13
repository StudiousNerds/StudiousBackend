package nerds.studiousTestProject.studycafe.dto.manage.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;

@Builder
@Data
public class ConvenienceInfoEditRequest {
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
