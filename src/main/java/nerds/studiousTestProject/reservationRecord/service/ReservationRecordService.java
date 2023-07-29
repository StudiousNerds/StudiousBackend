package nerds.studiousTestProject.reservationRecord.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.exception.ReservationRecordNotFoundException;
import nerds.studiousTestProject.exception.ReservationRecordNotFoundException;
import nerds.studiousTestProject.payment.dto.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.ReservationInfo;
import nerds.studiousTestProject.payment.dto.request.ReserveUser;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;
import nerds.studiousTestProject.reservationRecord.entity.ReservationStatus;
import nerds.studiousTestProject.reservationRecord.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.service.RoomService;
import nerds.studiousTestProject.user.entity.member.Member;
import nerds.studiousTestProject.user.service.member.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationRecordService {

    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomService roomService;
    private final MemberService memberService;

    @Transactional
    public String saveReservationRecordBeforePayment(PaymentRequest paymentRequest, Long roomId, String accessToken) {
        String orderId = String.valueOf(UUID.randomUUID());
        saveReservationRecord(memberService.getMemberFromAccessToken(accessToken),
                roomService.findRoomById(roomId),
                paymentRequest.getReservation(),
                paymentRequest.getUser(),
                orderId);
        return orderId;
    }

    private void saveReservationRecord(Member member, Room room, ReservationInfo reservation, ReserveUser user, String orderId) {
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
                        .room(room)
                        .orderId(orderId)
                        .member(member)
                        .build()
        );
    }

    public ReservationRecord findByOrderId(String orderId) {
        return reservationRecordRepository.findByOrderId(orderId)
                .orElseThrow(ReservationRecordNotFoundException::new);
    }

    @Transactional
    public void deleteByOrderId(String orderId){
        reservationRecordRepository.remove(findByOrderId(orderId));
    }

    @Transactional
    public void cancel(Long reservationRecordId){
        findById(reservationRecordId).canceled();
    }

    public ReservationRecord findById(Long id) {
        return reservationRecordRepository.findById(id).orElseThrow(ReservationRecordNotFoundException::new);
    }

}
