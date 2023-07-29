package nerds.studiousTestProject.payment.dto.confirm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveUserInfo {

    private String name;

    private String phoneNumber;

    private String request;

    public static ReserveUserInfo of(ReservationRecord reservationRecord) {
        return ReserveUserInfo.builder()
                .name(reservationRecord.getName())
                .phoneNumber(reservationRecord.getPhoneNumber())
                .request(reservationRecord.getRequest())
                .build();
    }

}
