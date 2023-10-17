package nerds.studiousTestProject.studycafe.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Getter
@Builder
public class PlaceInfo {

    private String studycafeName;
    private String roomName;
    private String address;

    public static PlaceInfo from(ReservationRecord reservationRecord) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        return PlaceInfo.builder()
                .studycafeName(studycafe.getName())
                .roomName(room.getName())
                .address(studycafe.getAddress().getEntryAddress())
                .build();
    }

    public static PlaceInfo of(Studycafe studycafe, Room room) {
        return PlaceInfo.builder()
                .studycafeName(studycafe.getName())
                .roomName(room.getName())
                .address(studycafe.getAddress().getEntryAddress())
                .build();
    }
}
