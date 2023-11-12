package nerds.studiousTestProject.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.payment.service.PaymentService;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.change.request.ChangeReservationRequest;
import nerds.studiousTestProject.reservation.dto.change.response.ShowChangeReservationResponse;
import nerds.studiousTestProject.reservation.dto.detail.response.ReservationDetailResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.MypageReservationResponse;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.NAME;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReserveRequest;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaymentResponse;
import nerds.studiousTestProject.reservation.dto.show.response.ReserveResponse;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReservationRecordController {

    private final PaymentService paymentService;
    private final ReservationRecordService reservationRecordService;


    @PostMapping("/reservations/{reservationId}/cancellations")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId,
                                       @RequestBody @Valid CancelRequest cancelRequest) {
        paymentService.userCancel(cancelRequest, reservationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/{roomId}")
    public ReserveResponse showReservationInfo(@LoggedInMember Long memberId, @PathVariable Long roomId) {
        return reservationRecordService.show(roomId, memberId);
    }

    @PostMapping("/rooms/{roomId}")
    public PaymentResponse reserve(
            @LoggedInMember Long memberId,
            @PathVariable Long roomId,
            @RequestBody @Valid ReserveRequest reserveRequest) {
        return reservationRecordService.reserve(reserveRequest, roomId, memberId);
    }

    @GetMapping("/reservations/{reservationId}/cancellations")
    public ReservationCancelResponse cancelInfo(@PathVariable Long reservationId) {
        return reservationRecordService.getCancelInfo(reservationId);
    }

    @GetMapping("/reservations")
    public MypageReservationResponse reservationSettingsInfoList(
            @LoggedInMember Long memberId,
            @RequestParam(defaultValue = NAME.ALL) ReservationSettingsStatus tab,
            @RequestParam(required = false) String studycafeName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam @PageableDefault(page = 1) Pageable pageable) {
        return reservationRecordService.getAll(tab, studycafeName, startDate, endDate, pageable, memberId);
    }

    @GetMapping("/reservations/{reservationRecordId}")
    public ReservationDetailResponse showDetail(@PathVariable Long reservationRecordId) {
        return reservationRecordService.showDetail(reservationRecordId);
    }

    @GetMapping("/reservations/{reservationRecordId}/changing")
    public ShowChangeReservationResponse showChangeReservation(@PathVariable Long reservationRecordId) {
        return reservationRecordService.showChangeReservation(reservationRecordId);
    }


    @PostMapping("/reservations/{reservationRecordId}/changing")
    public ResponseEntity<?> change(@PathVariable Long reservationRecordId, @RequestBody ChangeReservationRequest request) {
        PaymentResponse response = reservationRecordService.change(reservationRecordId, request);
        return response == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }
}
