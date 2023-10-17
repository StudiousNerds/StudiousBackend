package nerds.studiousTestProject.reservation.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.dto.ReserveUserInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Builder
@Getter
public class ShowAdminCancelResponse {

    private PlaceInfo place;
    private ReservationInfo reservation;
    private ReserveUserInfo user;

    public static ShowAdminCancelResponse from(ReservationRecord reservationRecord) {
        return ShowAdminCancelResponse.builder()
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationInfo.from(reservationRecord))
                .user(ReserveUserInfo.from(reservationRecord))
                .build();
    }

}
