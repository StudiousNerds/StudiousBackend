package nerds.studiousTestProject.reservation.dto.reserve.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.room.entity.Room;
import java.util.List;

@Data
@NoArgsConstructor
public class ReserveRequest {

    @NotNull
    private ReserveUser reserveUser;

    @NotNull
    private ReservationInfo reservationInfo;

    @Nullable
    private List<PaidConvenience> paidConveniences;

    public ReservationRecord toReservationRecord(Room room, Member member) {
        return ReservationRecord.builder()
                .userName(reserveUser.getName())
                .userPhoneNumber(reserveUser.getPhoneNumber())
                .date(reservationInfo.getDate())
                .startTime(reservationInfo.getStartTime())
                .endTime(reservationInfo.getEndTime())
                .request(reserveUser.getRequest())
                .headCount(reservationInfo.getHeadCount())
                .usingTime(reservationInfo.getUsingTime())
                .status(ReservationStatus.INPROGRESS)
                .room(room)
                .member(member)
                .build();
    }

}
