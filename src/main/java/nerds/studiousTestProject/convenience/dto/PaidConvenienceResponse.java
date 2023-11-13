package nerds.studiousTestProject.convenience.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceRecord;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaidConvenienceResponse {

    private String name;
    private int price;

    public static PaidConvenienceResponse from(Convenience convenience) {
        return new PaidConvenienceResponse(convenience.getName().name(), convenience.getPrice());
    }

    public static PaidConvenienceResponse from(ConvenienceRecord convenienceRecord) {
        return new PaidConvenienceResponse(convenienceRecord.getConvenienceName(), convenienceRecord.getPrice());
    }

    public ConvenienceRecord toConvenienceRecord(ReservationRecord reservationRecord) {
        return ConvenienceRecord.builder()
                .reservationRecord(reservationRecord)
                .convenienceName(this.name)
                .price(this.price)
                .build();
    }

}
