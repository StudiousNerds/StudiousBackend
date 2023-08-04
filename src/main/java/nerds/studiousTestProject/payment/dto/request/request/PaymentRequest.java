package nerds.studiousTestProject.payment.dto.request.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentRequest {

    private ReserveUser user;
    private ReservationInfo reservation;

}
