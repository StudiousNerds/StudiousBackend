package nerds.studiousTestProject.review.dto.available.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
public class AvailableReviewInfo {
    private Long studycafeId;
    private String studycafeName;
    private String studycafePhoto;
    private Long reservationId;
    private String roomName;
    private String paymentType;
    private Integer price;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer usingTime;
    private LocalDate validDate;

    public static AvailableReviewInfo of(ReservationRecord reservationRecord, Payment payment) {
        return AvailableReviewInfo.builder()
                .reservationId(reservationRecord.getId())
                .studycafeId(reservationRecord.getRoom().getStudycafe().getId())
                .studycafeName(reservationRecord.getRoom().getStudycafe().getName())
                .studycafePhoto(reservationRecord.getRoom().getStudycafe().getPhoto())
                .roomName(reservationRecord.getRoom().getName())
                .paymentType(payment.getMethod())
                .price(reservationRecord.getRoom().getPrice() * reservationRecord.getUsingTime())
                .date(reservationRecord.getDate())
                .startTime(reservationRecord.getStartTime())
                .endTime(reservationRecord.getEndTime())
                .usingTime(reservationRecord.getUsingTime())
                .validDate(reservationRecord.getDate().plusDays(7))
                .build();
    }
}