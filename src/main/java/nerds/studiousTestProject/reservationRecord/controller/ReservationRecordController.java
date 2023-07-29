package nerds.studiousTestProject.reservationRecord.controller;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.cancel.CancelRequest;
import nerds.studiousTestProject.payment.dto.cancel.CancelResponse;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.reservationRecord.service.ReservationRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
public class ReservationRecordController {

    private final PaymentService paymentService;

    @GetMapping("{reservationRecordId}/cancel")
    public List<CancelResponse> cancelReservation(@PathVariable Long reservationRecordId,
                                                  @RequestBody CancelRequest cancelRequest){
        return paymentService.cancel(cancelRequest, reservationRecordId);
    }
}
