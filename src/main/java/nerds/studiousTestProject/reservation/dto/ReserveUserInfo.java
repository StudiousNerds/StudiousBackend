package nerds.studiousTestProject.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Getter
@Builder
public class ReserveUserInfo {

    private String name;
    private String phoneNumber;

    public static ReserveUserInfo from(ReservationRecord reservationRecord) {
        return ReserveUserInfo.builder()
                .name(reservationRecord.getUserName())
                .phoneNumber(reservationRecord.getUserPhoneNumber())
                .build();
    }

}
