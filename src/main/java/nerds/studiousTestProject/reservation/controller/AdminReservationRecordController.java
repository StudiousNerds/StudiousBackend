package nerds.studiousTestProject.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.payment.util.totoss.AdminCancelRequest;
import nerds.studiousTestProject.reservation.dto.admin.ShowAdminCancelResponse;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequestMapping("/api/v1/admin/reservations")
@RequiredArgsConstructor
@RestController
public class AdminReservationRecordController {

    private final ReservationRecordService reservationRecordService;
    private final PaymentService paymentService;

    private static final String DEFAULT_PAGE_NUMBER = "1";

    @GetMapping("/{reservationId}/cancel")
    public ShowAdminCancelResponse showCancel(@PathVariable Long reservationId) {
        return reservationRecordService.showAdminCancel(reservationId);
    }

    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId, @RequestBody @Valid AdminCancelRequest request) {
        paymentService.adminCancel(request, reservationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public void show(@RequestParam(defaultValue = ViewCriteria.NAME.DATE_OF_USE) ViewCriteria viewCriteria,
                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                     @RequestParam(defaultValue = ReservationSettingsStatus.NAME.ALL) ReservationSettingsStatus reservationStatus,
                     @RequestParam(required = false) Long studycafeId,
                     @RequestParam(required = false) Long roomId,
                     @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) Integer page) {
        reservationRecordService.showAdmin(viewCriteria, startDate, endDate, reservationStatus, studycafeId, roomId, page);
    }

}
