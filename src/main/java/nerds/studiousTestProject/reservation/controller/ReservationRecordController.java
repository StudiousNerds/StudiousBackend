package nerds.studiousTestProject.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.reservation.dto.detail.response.ReservationDetailResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.MypageReservationResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus.TAB_NAME;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReserveRequest;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaymentInfoResponse;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.dto.show.response.ReserveResponse;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
public class ReservationRecordController {

    private final PaymentService paymentService;
    private final ReservationRecordService reservationRecordService;
    @PostMapping("/mypage/reservations/{reservationId}/cancellations")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId,
                                                  @RequestBody CancelRequest cancelRequest) {
        paymentService.cancel(cancelRequest, reservationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/studycafes/{studycafeId}/rooms/{roomId}")
    public ReserveResponse showReservationInfo(@PathVariable Long studycafeId, @PathVariable Long roomId, @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return reservationRecordService.show(studycafeId, roomId, accessToken);
    }

    @PostMapping("/reservations/studycafes/{studycafeId}/rooms/{roomId}")
    public PaymentInfoResponse reserve(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                       @PathVariable Long roomId,
                                       @RequestBody @Valid ReserveRequest reserveRequest) {
        return reservationRecordService.reserve(reserveRequest, roomId, accessToken);
    }

    @GetMapping("/mypage/reservations/{reservationId}/cancellations")
    public ReservationCancelResponse cancelReservationInfo(@PathVariable Long reservationId) {
        return reservationRecordService.getCancelInfo(reservationId);
    }

    @GetMapping("/mypage/reservations")
    public MypageReservationResponse reservationSettingsInfoList(@RequestParam(defaultValue = TAB_NAME.ALL) ReservationSettingsStatus tab,
                                                                 @RequestParam(required = false) String studycafeName,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                 @RequestParam(defaultValue = "1") Integer page,
                                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return reservationRecordService.getAll(tab, studycafeName, startDate, endDate, page, accessToken);
    }

    @GetMapping("/mypage/reservations/{reservationRecordId}")
    public ReservationDetailResponse showDetail(@PathVariable Long reservationRecordId) {
        return reservationRecordService.showDetail(reservationRecordId);
    }
}
