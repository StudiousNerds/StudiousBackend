package nerds.studiousTestProject.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountInfoResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/success")
    public ResponseEntity<Void> payConfirmSuccess(@RequestParam String orderId,
                                                       @RequestParam String paymentKey,
                                                       @RequestParam Integer amount) {
        Long reservationRecordId = paymentService.confirmSuccess(orderId, paymentKey, amount);
        return ResponseEntity.created(URI.create("/api/v1/mypage/reservations/" + reservationRecordId)).build();
    }

    @PostMapping("/fail")
    public ConfirmFailResponse payConfirmFail(@RequestParam String code,
                                              @RequestParam String message,
                                              @RequestParam String orderId) {
        return paymentService.confirmFail(message, orderId);
    }

    @GetMapping("/virtual/success")
    public VirtualAccountInfoResponse confirmVirtualAccount(@RequestParam String orderId,
                                                            @RequestParam String paymentKey,
                                                            @RequestParam Integer amount) {
        return paymentService.virtualAccount(orderId, paymentKey, amount);
    }


    @PostMapping("/deposit-callback")
    public ResponseEntity<Void> depositCallback(@RequestBody DepositCallbackRequest depositCallbackRequest) {
        paymentService.processDepositByStatus(depositCallbackRequest);
        return ResponseEntity.ok().build();
    }

}
