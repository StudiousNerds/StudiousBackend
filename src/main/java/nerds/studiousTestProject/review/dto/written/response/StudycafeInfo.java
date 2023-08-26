package nerds.studiousTestProject.review.dto.written.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudycafeInfo {
    private Long studycafeId;
    private String studycafeName;
    private String studycafePhoto;
    private String roomName;
    private LocalDate date;

    public static StudycafeInfo of(ReservationRecord reservationRecord) {
        return StudycafeInfo.builder()
                .studycafeId(reservationRecord.getRoom().getStudycafe().getId())
                .studycafeName(reservationRecord.getRoom().getStudycafe().getName())
                .studycafePhoto(reservationRecord.getRoom().getStudycafe().getPhoto())
                .roomName(reservationRecord.getRoom().getName())
                .date(reservationRecord.getDate())
                .build();
    }
}
