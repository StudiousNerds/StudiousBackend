package nerds.studiousTestProject.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountInfoResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import nerds.studiousTestProject.reservation.dto.detail.response.ReservationDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studious/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/success")
    public ReservationDetailResponse payConfirmSuccess(@ModelAttribute ConfirmSuccessRequest confirmSuccessRequest) {
        return paymentService.confirmSuccess(confirmSuccessRequest);
    }

    @GetMapping("/fail")
    public ConfirmFailResponse payConfirmFail(@RequestParam String code,
                                              @RequestParam String message,
                                              @RequestParam String orderId) {
        return paymentService.confirmFail(message, orderId);
    }

    @GetMapping("/virtual/success")
    public VirtualAccountInfoResponse confirmVirtualAccount(@ModelAttribute ConfirmSuccessRequest confirmSuccessRequest) {
        return paymentService.virtualAccount(confirmSuccessRequest);
    }


    @PostMapping("/deposit-callback")
    public ResponseEntity<Void> depositCallback(@RequestBody DepositCallbackRequest depositCallbackRequest) {
        paymentService.processDepositByStatus(depositCallbackRequest);
        return ResponseEntity.ok().build();
    }

}
