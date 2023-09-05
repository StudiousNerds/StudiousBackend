package nerds.studiousTestProject.payment.dto.confirm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmSuccessResponse {

    ReserveUserInfo reserveUserInfo;
    ReservationInfo reservationInfo;

    public static ConfirmSuccessResponse from(ReservationRecord reservationRecord) {
        return ConfirmSuccessResponse.builder()
                .reserveUserInfo(ReserveUserInfo.from(reservationRecord))
                .reservationInfo(ReservationInfo.from(reservationRecord)).build();
    }

}
