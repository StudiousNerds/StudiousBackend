package nerds.studiousTestProject.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountInfoResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/studious/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/success")
    public ResponseEntity<Void> payConfirmSuccess(@RequestParam String orderId,
                                            @RequestParam Integer amount,
                                            @RequestParam String paymentKey) {
        paymentService.confirmPayToToss(orderId, paymentKey, amount);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fail")
    public ConfirmFailResponse payConfirmFail(@RequestParam String code,
                                              @RequestParam String message,
                                              @RequestParam String orderId) {
        return paymentService.confirmFail(message, orderId);
    }

    @GetMapping("/virtual/success")
    public VirtualAccountInfoResponse confirmVirtualAccount(@RequestParam String orderId,
                                                            @RequestParam Integer amount,
                                                            @RequestParam String paymentKey) {
        return paymentService.virtualAccount(orderId, paymentKey, amount);
    }


    @PostMapping("/deposit-callback")
    public ResponseEntity<Void> depositCallback(@RequestBody DepositCallbackRequest depositCallbackRequest) {
        paymentService.processDepositByStatus(depositCallbackRequest);
        return ResponseEntity.ok().build();
    }

}
