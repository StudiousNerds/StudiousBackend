package nerds.studiousTestProject.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.confirm.response.SuccessPayResponse;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountInfoResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/success")
    public SuccessPayResponse payConfirmSuccess(@RequestBody @Valid ConfirmSuccessRequest request) {
        return paymentService.confirmSuccess(request);
    }

    @PostMapping("/fail")
    public ConfirmFailResponse payConfirmFail(@RequestParam String code,
                                              @RequestParam String message,
                                              @RequestParam String orderId) {
        return paymentService.confirmFail(message, orderId);
    }

    @PostMapping("/virtual/success")
    public VirtualAccountInfoResponse confirmVirtualAccount(@RequestBody @Valid ConfirmSuccessRequest request) {
        return paymentService.virtualAccount(request);
    }


    @PostMapping("/deposit-callback")
    public ResponseEntity<Void> depositCallback(@RequestBody DepositCallbackRequest depositCallbackRequest) {
        paymentService.processDepositByStatus(depositCallbackRequest);
        return ResponseEntity.ok().build();
    }

}
