package nerds.studiousTestProject.payment.dto.confirm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmResponse {

    ReserveUserInfo reserveUserInfo;
    ReservationInfo reservationInfo;

}
