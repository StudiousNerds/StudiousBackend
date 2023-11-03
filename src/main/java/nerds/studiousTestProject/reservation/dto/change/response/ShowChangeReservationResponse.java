package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.dto.PaidConvenienceInfo;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.room.entity.PriceType;
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
    private int maxHeadCount;
    private PriceType priceType;
    private PaidConvenienceInfoForChange paidConveniences;
    private int usingPrice;

    public static ShowChangeReservationResponse of(ReservationRecord reservationRecord, List<PaidConvenienceInfo> paidConvenienceList, List<PaidConvenienceInfo> notPaidConvenienceList) {
        Room room = reservationRecord.getRoom();
        return ShowChangeReservationResponse.builder()
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationInfo.from(reservationRecord))
                .headCount(reservationRecord.getHeadCount())
                .maxHeadCount(room.getMaxHeadCount())
                .priceType(room.getPriceType())
                .usingPrice(room.getPrice())
                .paidConveniences(PaidConvenienceInfoForChange.of(paidConvenienceList, notPaidConvenienceList))
                .build();
    }

}
