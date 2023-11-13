package nerds.studiousTestProject.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Getter
@Builder
public class ReserveUserResponse {

    private String name;
    private String phoneNumber;

    public static ReserveUserResponse from(ReservationRecord reservationRecord) {
        return ReserveUserResponse.builder()
                .name(reservationRecord.getUserName())
                .phoneNumber(reservationRecord.getUserPhoneNumber())
                .build();
    }

    public static ReserveUserResponse from(Member member) {
        return ReserveUserResponse.builder()
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

}
