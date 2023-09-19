package nerds.studiousTestProject.reservation.dto.mypage.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.time.LocalDate;
import java.time.LocalTime;

import static nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus.AFTER_USING;
import static nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus.BEFORE_USING;
import static nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus.CANCELED;
import static nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus.USING;

@Getter
@AllArgsConstructor
@Builder
public class ReservationRecordInfoWithStatus {

    private String studycafeName;
    private String studycafePhoto;
    private String roomName;
    private Long reservationId;
    private LocalDate reservationDate;
    private LocalTime reservationStartTime;
    private LocalTime reservationEndTime;
    private int usingTime;
    private int price;
    private String paymentMethod;
    private String reservationStatus; //이용전, 이용중,
    private String cancelReason;

    public static ReservationRecordInfoWithStatus of(Studycafe studycafe, Room room, ReservationRecord reservationRecord, Payment payment) {
        return ReservationRecordInfoWithStatus.builder()
                .studycafeName(studycafe.getName())
                .studycafePhoto(studycafe.getPhoto())
                .roomName(room.getName())
                .reservationId(reservationRecord.getId())
                .reservationDate(reservationRecord.getDate())
                .reservationStartTime(reservationRecord.getStartTime())
                .reservationEndTime(reservationRecord.getEndTime())
                .usingTime(reservationRecord.getUsingTime())
                .price(payment.getPrice())
                .paymentMethod(payment.getMethod())
                .cancelReason(payment.getCancelReason())
                .reservationStatus(getReservationSettingsResponse(reservationRecord).name())
                .build();
    }

    private static ReservationSettingsStatus getReservationSettingsResponse(ReservationRecord reservationRecord) {
        LocalDate reserveDate = reservationRecord.getDate();
        if (reservationRecord.getStatus() == ReservationStatus.CANCELED) return CANCELED;
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        if (reserveDate.isBefore(nowDate) || (reserveDate == nowDate && reservationRecord.getStartTime().isAfter(nowTime)))
            return AFTER_USING;
        if (reserveDate.isAfter(nowDate) || (reserveDate == nowDate && reservationRecord.getEndTime().isBefore(nowTime)))
            return BEFORE_USING;
        return USING;
    }

}
