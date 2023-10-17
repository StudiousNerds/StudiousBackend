package nerds.studiousTestProject.reservation.dto.mypage.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.dto.PaymentInfo;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.dto.ReservationInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;

@Getter
@AllArgsConstructor
@Builder
public class ReservationRecordInfoWithStatus {

    private Long reservationId;
    private PlaceInfo place;
    private ReservationInfo reservation;
    private PaymentInfo payment;
    private String reservationStatus;
    private CancelInfo cancel;

    public static ReservationRecordInfoWithStatus of(ReservationRecord reservationRecord, Payment payment, ReservationSettingsStatus status) {
        return ReservationRecordInfoWithStatus.builder()
                .reservationId(reservationRecord.getId())
                .place(PlaceInfo.from(reservationRecord))
                .reservation(ReservationInfo.from(reservationRecord))
                .payment(PaymentInfo.from(payment))
                .cancel(CancelInfo.from(payment))
                .reservationStatus(status.name())
                .build();
    }

}
