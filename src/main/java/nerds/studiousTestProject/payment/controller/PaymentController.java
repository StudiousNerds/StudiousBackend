package nerds.studiousTestProject.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.payment.dto.callback.request.DepositCallbackRequest;
import nerds.studiousTestProject.payment.dto.confirm.response.ConfirmFailResponse;
import nerds.studiousTestProject.payment.dto.confirm.response.SuccessPayResponse;
import nerds.studiousTestProject.payment.dto.virtual.response.VirtualAccountResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.util.totoss.ConfirmSuccessRequest;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments/success")
    public SuccessPayResponse payConfirmSuccess(@RequestBody @Valid ConfirmSuccessRequest request) {
        return paymentService.confirmSuccess(request);
    }

    @PostMapping("/payments/fail")
    public ConfirmFailResponse payConfirmFail(@RequestParam String code,
                                              @RequestParam String message,
                                              @RequestParam String orderId) {
        return paymentService.confirmFail(message, orderId);
    }

    @PostMapping("/paymentsvirtual/success")
    public VirtualAccountResponse confirmVirtualAccount(@RequestBody @Valid ConfirmSuccessRequest request) {
        return paymentService.virtualAccount(request);
    }


    @PostMapping("/payments/deposit-callback")
    public ResponseEntity<Void> depositCallback(@RequestBody DepositCallbackRequest depositCallbackRequest) {
        paymentService.processDepositByStatus(depositCallbackRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reservations/{reservationId}/cancellations")
    public ReservationCancelResponse showCancel(@PathVariable Long reservationId) {
        return paymentService.getCancel(reservationId);
    }

    @PostMapping("/reservations/{reservationId}/cancellations")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId,
                                       @RequestBody @Valid CancelRequest cancelRequest) {
        paymentService.userCancel(cancelRequest, reservationId);
        return ResponseEntity.noContent().build();
    }

}
