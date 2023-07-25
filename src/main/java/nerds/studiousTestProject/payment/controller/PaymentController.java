package nerds.studiousTestProject.payment.controller;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.confirm.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.confirm.ConfirmSuccessResponse;
import nerds.studiousTestProject.payment.dto.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.PaymentResponse;
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
        String orderId = reservationRecordService.saveReservationRecordBeforePayment(paymentRequest, roomId);
        return paymentService.createPaymentResponse(paymentRequest, orderId);
    }

    @GetMapping("/success")
    public ConfirmSuccessResponse payConfirmSuccess(@RequestParam String orderId,
                                                    @RequestParam int amount,
                                                    @RequestParam String paymentKey) {
        return paymentService.confirmPayToToss(orderId, paymentKey, amount);
    }

    @GetMapping("/fail")
    public ConfirmFailResponse payConfirmFail(@RequestParam String code,
                                              @RequestParam String message,
                                              @RequestParam String orderId) {

        return paymentService.confirmFail(message, orderId);
    }

}
