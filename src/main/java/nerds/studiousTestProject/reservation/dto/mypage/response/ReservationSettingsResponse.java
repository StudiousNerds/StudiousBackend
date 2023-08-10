package nerds.studiousTestProject.reservation.dto.mypage.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class ReservationSettingsResponse {

    private String studycafeName;
    private String studycafePhoto;
    private String roomName;
    private LocalDate reservationDate;
    private LocalTime reservationStartTime;
    private LocalTime reservationEndTime;
    private int usingTime;
    private int price;
    private String paymentMethod;
    private String reservationStatus; //이용전, 이용중,
    private String cancelReason;

}
