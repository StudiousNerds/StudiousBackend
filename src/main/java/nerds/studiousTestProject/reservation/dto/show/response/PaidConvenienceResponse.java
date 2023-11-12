package nerds.studiousTestProject.reservation.dto.show.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.entity.Convenience;

@Builder
@AllArgsConstructor
@Getter
public class PaidConvenienceResponse {

    private String name;
    private Integer price;

    public static PaidConvenienceResponse from(Convenience convenienceList) {
        return PaidConvenienceResponse.builder()
                .name(convenienceList.getName().name())
                .price(convenienceList.getPrice())
                .build();
    }

}
