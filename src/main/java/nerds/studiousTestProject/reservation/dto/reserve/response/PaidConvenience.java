package nerds.studiousTestProject.reservation.dto.reserve.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.entity.ConvenienceList;

@Builder
@AllArgsConstructor
@Getter
public class PaidConvenience {

    private String convenienceName;
    private Integer price;

    public static PaidConvenience from(ConvenienceList convenienceList) {
        return PaidConvenience.builder()
                .convenienceName(convenienceList.getName().name())
                .price(convenienceList.getPrice())
                .build();
    }

}
