package nerds.studiousTestProject.payment.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentRequest {

    private ReserveUser user;
    private ReservationInfo reservation;
}
