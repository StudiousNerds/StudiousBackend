package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaidConvenienceInfo {

    private String name;
    private int price;

}
