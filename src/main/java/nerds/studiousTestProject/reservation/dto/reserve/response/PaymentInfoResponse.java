package nerds.studiousTestProject.reservation.dto.reserve.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReserveRequest;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentInfoResponse {

    private int amount;

    private String orderId;

    private String orderName;

    private String successUrl;

    private String failUrl;

    private static final String REQUEST_SUCCESS_URI = "http://localhost:8080/studious/payments/success";
    private static final String REQUEST_FAIL_URI = "http://localhost:8080/studious/payments/fail";
    public static PaymentInfoResponse of(ReserveRequest reserveRequest, ReservationRecord reservationRecord) {
        return PaymentInfoResponse.builder()
                .amount(reserveRequest.getReservationInfo().getPrice())
                .orderId(reservationRecord.getOrderId())
                .orderName(reservationRecord.getUserName())
                .successUrl(REQUEST_SUCCESS_URI)
                .failUrl(REQUEST_FAIL_URI)
                .build();
    }

}



