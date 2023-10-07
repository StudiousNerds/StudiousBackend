package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payments;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowChangeReservationResponse {

    private ReservationWithPlaceAndPayInfo info;
    private int headCount;
    private List<PaidConvenienceInfo> paidList;
    private List<PaidConvenienceInfo> notPaidList;

    public static ShowChangeReservationResponse of(ReservationRecord reservationRecord, int price, List<PaidConvenienceInfo> paidConvenienceList, List<PaidConvenienceInfo> notPaidConvenienceList) {
        return ShowChangeReservationResponse.builder()
                .info(ReservationWithPlaceAndPayInfo.of(price, reservationRecord))
                .headCount(reservationRecord.getHeadCount())
                .paidList(paidConvenienceList)
                .notPaidList(notPaidConvenienceList)
                .build();
    }

}
