package nerds.studiousTestProject.reservation.dto.mypage.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.dto.PaymentResponse;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.dto.ReservationResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus;
import nerds.studiousTestProject.studycafe.dto.PlaceResponse;

@Getter
@AllArgsConstructor
@Builder
public class ReservationRecordWithStatusResponse {

    private Long id;
    private PlaceResponse place;
    private ReservationResponse reservation;
    private PaymentResponse payment;
    private String reservationStatus;
    private CancelResponse cancelResponse;

    public static ReservationRecordWithStatusResponse of(ReservationRecord reservationRecord, Payment payment, ReservationSettingsStatus status) {
        return ReservationRecordWithStatusResponse.builder()
                .id(reservationRecord.getId())
                .place(PlaceResponse.from(reservationRecord))
                .reservation(ReservationResponse.from(reservationRecord))
                .payment(PaymentResponse.from(payment))
                .cancelResponse(CancelResponse.from(payment))
                .reservationStatus(status.name())
                .build();
    }

}
