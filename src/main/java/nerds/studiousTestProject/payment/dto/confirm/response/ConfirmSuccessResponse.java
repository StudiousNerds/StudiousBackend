package nerds.studiousTestProject.payment.dto.confirm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.dto.ReserveUserInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmSuccessResponse {

    private PlaceInfo place;
    private ReservationInfo reservation;
    private ReserveUserInfo user;

    public static ConfirmSuccessResponse from(ReservationRecord reservationRecord) {
        return ConfirmSuccessResponse.builder()
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationInfo.from(reservationRecord))
                .user(ReserveUserInfo.from(reservationRecord))
                .build();
    }

}
