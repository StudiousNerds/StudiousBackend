package nerds.studiousTestProject.reservation.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.service.member.MemberService;
import nerds.studiousTestProject.payment.dto.request.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.request.ReservationInfo;
import nerds.studiousTestProject.payment.dto.request.request.ReserveUser;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInResponse;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.reserve.response.ReserveResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_RESERVATION_CANCEL_DATE;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationRecordService {

    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomRepository roomRepository;
    private final MemberService memberService;
    private final StudycafeRepository studycafeRepository;

    @Transactional
    public String saveReservationRecordBeforePayment(PaymentRequest paymentRequest, Long roomId, String accessToken) {
        String orderId = String.valueOf(UUID.randomUUID());
        saveReservationRecord(
                memberService.getMemberFromAccessToken(accessToken),
                findRoomById(roomId),
                paymentRequest.getReservation(),
                paymentRequest.getUser(),
                orderId);
        return orderId;
    }

    private void saveReservationRecord(Member member, Room room, ReservationInfo reservation, ReserveUser user, String orderId) {
        reservationRecordRepository.save(
                ReservationRecord.builder()
                        .status(ReservationStatus.INPROGRESS)
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
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    @Transactional
    public void deleteByOrderId(String orderId){
        reservationRecordRepository.delete(findByOrderId(orderId));
    }

    @Transactional
    public void cancel(Long reservationRecordId){
        findById(reservationRecordId).canceled();
    }

    public ReservationRecord findById(Long reservationRecordId) {
        return reservationRecordRepository.findById(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    public Room findRoomById(Long roomId){
        return roomRepository.findById(roomId)
                .orElseThrow(()->new NotFoundException(NOT_FOUND_ROOM));
    }

    public Studycafe findStudycafeById(Long studycafeId) {
        return studycafeRepository.findById(studycafeId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    public ReserveResponse reserve(Long cafeId, Long roomId, String accessToken) {
        Member member = memberService.getMemberFromAccessToken(accessToken);
        Room room = findRoomById(roomId);
        Studycafe studycafe = findStudycafeById(cafeId);
        return ReserveResponse.of(member, room, studycafe);
    }

    public ReservationCancelResponse cancelInfo(Long reservationId) {
        ReservationRecord reservationRecord = findById(reservationId);
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        List<RefundPolicy> refundPolicies = studycafe.getRefundPolicies();
        Payment payment = reservationRecord.getPayment();

        LocalDate reservationDate = reservationRecord.getDate();
        final int remainDate = getRemainDate(reservationDate, LocalDate.now());

        RefundPolicy refundPolicyOnDay = refundPolicies.stream()
                .filter(refundPolicy -> refundPolicy.getRefundDay().getRemain() == remainDate)
                .findFirst()
                .orElseThrow(() -> new BadRequestException(INVALID_RESERVATION_CANCEL_DATE));

        Integer totalPrice = payment.getPrice();
        int refundFee = totalPrice * refundPolicyOnDay.getRate() * (1 / 100);
        int refundPrice = totalPrice - refundFee;

        return ReservationCancelResponse.builder()
                .studycafeName(studycafe.getName())
                .roomName(room.getName())
                .reservationDate(reservationDate)
                .reservationDuration(reservationRecord.getDuration())
                .reservationStartTime(reservationRecord.getStartTime())
                .reservationEndTime(reservationRecord.getEndTime())
                .price(totalPrice)
                .refundPolicy(refundPolicies.stream().map(RefundPolicyInResponse::from).collect(Collectors.toList()))
                .paymentMethod(payment.getMethod())
                .refundPrice(refundPrice)
                .refundFee(refundFee)
                .refundPolicyOnDay(RefundPolicyInResponse.from(refundPolicyOnDay))
                .build();

    }

    private static int getRemainDate(LocalDate reservationDate, LocalDate now) {
        int remainDate = reservationDate.getDayOfYear() - now.getDayOfYear();
        return remainDate > 8 ? 8 : remainDate;
    }
}
