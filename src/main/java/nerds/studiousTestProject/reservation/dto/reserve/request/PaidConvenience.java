package nerds.studiousTestProject.reservation.dto.reserve.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.entity.ConvenienceRecord;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class PaidConvenience {

    @NotBlank
    private String convenienceName;

    @NotBlank
    private Integer price;

    public ConvenienceRecord toConvenienceRecord(ReservationRecord reservationRecord) {
        return ConvenienceRecord.builder()
                .reservationRecord(reservationRecord)
                .convenienceName(convenienceName)
                .price(price)
                .build();
    }


}
