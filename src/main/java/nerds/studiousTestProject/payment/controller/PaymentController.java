package nerds.studiousTestProject.payment.controller;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.PaymentRequest;
import nerds.studiousTestProject.payment.dto.PaymentResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.reservationRecord.service.ReservationRecordService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/studious/payments")
public class PaymentController {

    private final ReservationRecordService reservationRecordService;
    private final PaymentService paymentService;

    @PostMapping("/studycafes/{cafeId}/rooms/{roomId}")
    public PaymentResponse payRequest(@PathVariable Long roomId, @RequestBody PaymentRequest paymentRequest) {
        reservationRecordService.saveReservationRecordBeforePayment(paymentRequest, roomId);
        return paymentService.createPaymentResponse(paymentRequest);
    }

}
