package nerds.studiousTestProject.reservation.dto.detail.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class PlaceInfo {

    private String studycafeName;
    private String roomName;
    private LocalDate date
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;

    public static PlaceInfo of(Studycafe studycafe, Room room, ReservationRecord reservationRecord) {
        return PlaceInfo.builder()
                .studycafeName(studycafe.getName())
                .roomName(room.getName())
                .date(reservationRecord.getDate())
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .address(studycafe.getAddress().getEntryAddress())
                .build();
    }

}
