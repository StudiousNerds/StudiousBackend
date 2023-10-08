package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceRecord;

@AllArgsConstructor
@Getter
public class PaidConvenienceInfo {

    private String name;
    private int price;

    public static PaidConvenienceInfo from(Convenience convenience) {
        return new PaidConvenienceInfo(convenience.getName().name(), convenience.getPrice());
    }

    public static PaidConvenienceInfo from(ConvenienceRecord convenienceRecord) {
        return new PaidConvenienceInfo(convenienceRecord.getConvenienceName(), convenienceRecord.getPrice());
    }

}
