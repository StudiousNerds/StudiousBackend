package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.dto.PaidConvenienceResponse;
import nerds.studiousTestProject.reservation.dto.ReservationResponse;
import nerds.studiousTestProject.room.entity.PriceType;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class ShowChangeReservationResponse {

    private PlaceInfo place;
    private ReservationResponse reservation;
    private int headCount;
    private int maxHeadCount;
    private PriceType priceType;
    private PaidConvenienceForChangeResponse paidConveniences;
    private int usingPrice;

    public static ShowChangeReservationResponse of(ReservationRecord reservationRecord, List<PaidConvenienceResponse> paidConvenienceList, List<PaidConvenienceResponse> notPaidConvenienceList) {
        Room room = reservationRecord.getRoom();
        return ShowChangeReservationResponse.builder()
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationResponse.from(reservationRecord))
                .headCount(reservationRecord.getHeadCount())
                .maxHeadCount(room.getMaxHeadCount())
                .priceType(room.getPriceType())
                .usingPrice(room.getPrice())
                .paidConveniences(PaidConvenienceForChangeResponse.of(paidConvenienceList, notPaidConvenienceList))
                .build();
    }

}
