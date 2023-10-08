package nerds.studiousTestProject.reservation.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.dto.ReserveUserInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@AllArgsConstructor
@Builder
@Getter
public class ShowAdminCancelResponse {

    private PlaceInfo place;
    private ReservationInfo reservation;
    private ReserveUserInfo user;

    public static ShowAdminCancelResponse from(ReservationRecord reservationRecord) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        return ShowAdminCancelResponse.builder()
                .place(PlaceInfo.of(studycafe, room))
                .reservation(ReservationInfo.from(reservationRecord))
                .user(ReserveUserInfo.from(reservationRecord))
                .build();
    }

}
