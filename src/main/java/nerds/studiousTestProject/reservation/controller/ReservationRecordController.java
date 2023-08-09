package nerds.studiousTestProject.reservation.controller;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.dto.cancel.response.CancelResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.reserve.response.ReserveResponse;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
public class ReservationRecordController {

    private final PaymentService paymentService;
    private final ReservationRecordService reservationRecordService;

    @PostMapping("{reservationRecordId}/cancel")
    public List<CancelResponse> cancelReservation(@PathVariable Long reservationRecordId,
                                                  @RequestBody CancelRequest cancelRequest){
        return paymentService.cancel(cancelRequest, reservationRecordId);
    }

    @GetMapping("/reservations/studycafes/{cafeId}/rooms/{roomId}")
    public ReserveResponse reserve(@PathVariable Long cafeId, @PathVariable Long roomId, @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return reservationRecordService.reserve(cafeId, roomId, accessToken);
    }

    @GetMapping("/mypage/reservation-settings/{reservationId}/cancellations")
    public ReservationCancelResponse cancelReservation(@PathVariable Long reservationId){
        return reservationRecordService.cancelInfo(reservationId);
    }

}
