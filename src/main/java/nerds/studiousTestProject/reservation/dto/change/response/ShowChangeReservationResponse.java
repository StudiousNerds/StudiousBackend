package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.dto.PaidConvenienceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class ShowChangeReservationResponse {

    private PlaceInfo place;
    private ReservationInfo reservation;
    private int headCount;
    private List<PaidConvenienceInfo> paidList;
    private List<PaidConvenienceInfo> notPaidList;

    public static ShowChangeReservationResponse of(ReservationRecord reservationRecord, int price, List<PaidConvenienceInfo> paidConvenienceList, List<PaidConvenienceInfo> notPaidConvenienceList) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        return ShowChangeReservationResponse.builder()
                .place(PlaceInfo.of(studycafe, room))
                .reservation(ReservationInfo.from(reservationRecord))
                .headCount(reservationRecord.getHeadCount())
                .paidList(paidConvenienceList)
                .notPaidList(notPaidConvenienceList)
                .build();
    }

}
