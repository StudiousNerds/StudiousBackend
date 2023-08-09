package nerds.studiousTestProject.reservation.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class ReservationCancelResponse {
    private String studycafeName;
    private String roomName;
    private LocalDate reservationDate;
    private LocalTime reservationStartTime;
    private LocalTime reservationEndTime;
    private int reservationDuration;
    private List<RefundPolicyInResponse> refundPolicy;
    private String paymentMethod;
    private int price;
    private int refundPrice;
    private int refundFee;
    private RefundPolicyInResponse refundPolicyOnDay;
}
