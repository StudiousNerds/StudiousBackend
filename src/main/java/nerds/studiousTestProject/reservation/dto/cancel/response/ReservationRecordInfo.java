package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Builder
public class ReservationRecordInfo {
    private String studycafeName;
    private String studycafePhoto;
    private String roomName;
    private LocalDate reservationDate;
    private LocalTime reservationStartTime;
    private LocalTime reservationEndTime;
    private int reservationDuration;

    public static ReservationRecordInfo of(Studycafe studycafe, Room room, ReservationRecord reservationRecord) {
        return ReservationRecordInfo.builder()
                .studycafeName(studycafe.getName())
                .studycafePhoto(studycafe.getPhoto())
                .roomName(room.getName())
                .reservationDate(reservationRecord.getDate())
                .reservationStartTime(reservationRecord.getStartTime())
                .reservationEndTime(reservationRecord.getEndTime())
                .reservationDuration(reservationRecord.getDuration())
                .build();
    }

}
