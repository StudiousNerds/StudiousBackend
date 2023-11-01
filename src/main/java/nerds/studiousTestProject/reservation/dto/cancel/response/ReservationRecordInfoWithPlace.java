package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@AllArgsConstructor
@Getter
@Builder
public class ReservationRecordInfoWithPlace {

    private String studycafeName;
    private String studycafePhoto;
    private String roomName;
    private ReservationInfo reservation;

    public static ReservationRecordInfoWithPlace of(Studycafe studycafe, Room room, ReservationRecord reservationRecord) {
        return ReservationRecordInfoWithPlace.builder()
                .studycafeName(studycafe.getName())
                .studycafePhoto(studycafe.getPhoto())
                .roomName(room.getName())
                .reservation(ReservationInfo.from(reservationRecord))
                .build();
    }

}
