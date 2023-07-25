package nerds.studiousTestProject.reservationRecord.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.ReservationInfo;
import nerds.studiousTestProject.payment.dto.request.ReserveUser;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;
import nerds.studiousTestProject.reservationRecord.entity.ReservationStatus;
import nerds.studiousTestProject.reservationRecord.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationRecordService {

    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomService roomService;

    @Transactional
    public String saveReservationRecordBeforePayment(PaymentRequest paymentRequest, Long roomId){
        ReservationInfo reservation = paymentRequest.getReservation();
        ReserveUser user = paymentRequest.getUser();
        String orderId = String.valueOf(UUID.randomUUID());
        reservationRecordRepository.saveReservationRecord(
                ReservationRecord.builder()
                        .reservationStatus(ReservationStatus.INPROGRESS)
                        .date(reservation.getReserveDate())
                        .duration(reservation.getDuration())
                        .startTime(reservation.getStartTime())
                        .endTime(reservation.getEndTime())
                        .headCount(reservation.getHeadCount())
                        .name(user.getName())
                        .phoneNumber(user.getPhoneNumber())
                        .request(user.getRequest())
                        .room(roomService.findById(roomId))
                        .orderId(orderId)
                        .build()
        );
        return orderId;
    }

    public ReservationRecord findByOrderId(String orderId) {
        return reservationRecordRepository.findByOrderId(orderId);
    }

    public void deleteByOrderId(String orderId){
        reservationRecordRepository.deleteByOrderId(orderId);
    }
}
