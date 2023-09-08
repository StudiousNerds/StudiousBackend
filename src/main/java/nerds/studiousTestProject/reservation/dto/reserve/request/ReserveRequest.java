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
import java.util.UUID;

@Data
@NoArgsConstructor
public class ReserveRequest {

    @NotNull
    private ReserveUser user;

    @NotNull
    private ReservationInfo reservation;

    @Nullable
    private List<PaidConvenience> paidConveniences;

    public ReservationRecord toReservationRecord(Room room, Member member) {
        return ReservationRecord.builder()
                .userName(user.getName())
                .userPhoneNumber(user.getPhoneNumber())
                .date(reservation.getReserveDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .request(user.getRequest())
                .headCount(reservation.getHeadCount())
                .usingTime(reservation.getUsingTime())
                .status(ReservationStatus.INPROGRESS)
                .room(room)
                .orderId(UUID.randomUUID().toString())
                .member(member)
                .build();
    }

}
