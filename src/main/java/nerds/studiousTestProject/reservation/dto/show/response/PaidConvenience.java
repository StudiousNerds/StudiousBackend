package nerds.studiousTestProject.reservation.dto.show.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.entity.Convenience;

@Builder
@AllArgsConstructor
@Getter
public class PaidConvenience {

    private String convenienceName;
    private Integer price;

    public static PaidConvenience from(Convenience convenienceList) {
        return PaidConvenience.builder()
                .convenienceName(convenienceList.getName().name())
                .price(convenienceList.getPrice())
                .build();
    }

}
