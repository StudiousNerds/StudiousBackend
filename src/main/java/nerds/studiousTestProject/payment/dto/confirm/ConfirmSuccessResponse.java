package nerds.studiousTestProject.payment.dto.confirm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmSuccessResponse {

    ReserveUserInfo reserveUserInfo;
    ReservationInfo reservationInfo;

}