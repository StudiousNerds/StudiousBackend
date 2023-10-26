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
    private String studycafePhoto;
    private String roomName;
    private String address;

    public static PlaceInfo from(ReservationRecord reservationRecord) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        return createPlaceInfo(studycafe, room);
    }

    public static PlaceInfo of(Studycafe studycafe, Room room) {
        return createPlaceInfo(studycafe, room);
    }

    private static PlaceInfo createPlaceInfo(Studycafe studycafe, Room room) {
        return PlaceInfo.builder()
                .studycafeName(studycafe.getName())
                .studycafePhoto(studycafe.getPhoto())
                .roomName(room.getName())
                .address(studycafe.getAddress().getEntryAddress())
                .build();
    }
}
