package nerds.studiousTestProject.reservationRecord.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.dto.PaymentRequest;
import nerds.studiousTestProject.payment.dto.ReservationInfo;
import nerds.studiousTestProject.payment.dto.ReserveUser;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;
import nerds.studiousTestProject.reservationRecord.entity.ReservationStatus;
import nerds.studiousTestProject.reservationRecord.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationRecordService {

    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomService roomService;

    @Transactional
    public void saveReservationRecordBeforePayment(PaymentRequest paymentRequest, Long roomId){
        ReservationInfo reservation = paymentRequest.getReservation();
        ReserveUser user = paymentRequest.getUser();
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
                        .build()
        );
    }
}
