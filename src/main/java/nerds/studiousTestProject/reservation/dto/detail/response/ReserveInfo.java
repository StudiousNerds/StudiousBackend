package nerds.studiousTestProject.reservation.dto.detail.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Getter
@Builder
public class ReserveInfo {

    private String username;
    private String phoneNumber;

    public static ReserveInfo from(ReservationRecord reservationRecord) {
        return ReserveInfo.builder()
                .username(reservationRecord.getUserName())
                .phoneNumber(reservationRecord.getUserPhoneNumber())
                .build();
    }

}
