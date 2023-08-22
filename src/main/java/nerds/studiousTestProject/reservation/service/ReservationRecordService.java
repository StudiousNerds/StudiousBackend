package nerds.studiousTestProject.reservation.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.service.MemberService;
import nerds.studiousTestProject.payment.dto.request.request.PaymentRequest;
import nerds.studiousTestProject.payment.dto.request.request.ReservationInfo;
import nerds.studiousTestProject.payment.dto.request.request.ReserveUser;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.cancel.response.PaymentInfo;
import nerds.studiousTestProject.reservation.dto.cancel.response.RefundPolicyInfo;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationRecordInfo;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.dto.reserve.response.ReserveResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_RESERVATION_CANCEL_DATE;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationRecordService {

    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomRepository roomRepository;
    private final MemberService memberService;
    private final StudycafeRepository studycafeRepository;
    private Map<Integer, Boolean> reservationTimes = new ConcurrentHashMap<>();


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
                        .userName(user.getName())
                        .userPhoneNumber(user.getPhoneNumber())
                        .request(user.getRequest())
                        .room(room)
                        .orderId(orderId)
                        .member(member)
                        .build()
        );
    }

    public Map<Integer, Boolean> getReservationTimes(LocalDate date, Long studycafeId, Long roomId) {
        Studycafe studycafe = studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        LocalTime openTime = studycafe.getOperationInfos().get(0).getStartTime();
        LocalTime endTime = studycafe.getOperationInfos().get(1).getEndTime();

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
        Integer minUsingTime = room.getMinUsingTime() / 60;

        for (int i = openTime.getHour(); i <= endTime.getHour(); i += minUsingTime) {
            reservationTimes.put(i, true);
        }

        List<Object[]> allReservedTime = reservationRecordRepository.findAllReservedTime(date, roomId);
        for (Object[] localTimes : allReservedTime) {
            for (int i = ((LocalTime) localTimes[0]).getHour(); i < ((LocalTime) localTimes[1]).getHour(); i++) {
                reservationTimes.put(i, false);
            }
        }
        return reservationTimes;
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

    public List<ReservationRecord> findAllByMemberId(Long memberId) {
        return reservationRecordRepository.findAllByMemberId(memberId);
    }

    public List<ReservationRecord> findAllByRoomId(Long roomId) {
        return reservationRecordRepository.findAllByRoomId(roomId);
    }

    public List<ReservationRecord> findAllByStudycafeId(Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }
        
    public ReservationCancelResponse cancelInfo(Long reservationId) {
        ReservationRecord reservationRecord = findById(reservationId);
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        List<RefundPolicy> refundPolicies = studycafe.getRefundPolicies();
        Payment payment = reservationRecord.getPayment();

        final int remainDate = getRemainDate(reservationRecord.getDate(), LocalDate.now());
        RefundPolicy refundPolicyOnDay = getRefundPolicyOnDay(refundPolicies, remainDate);

        return ReservationCancelResponse.builder()
                .reservationInfo(ReservationRecordInfo.of(studycafe, room, reservationRecord))
                .paymentInfo(calculateRefundMoney(payment, refundPolicyOnDay))
                .refundPolicyInfo(RefundPolicyInfo.of(refundPolicies, refundPolicyOnDay))
                .build();

    }

    private PaymentInfo calculateRefundMoney(Payment payment, RefundPolicy refundPolicyOnDay) {
        Integer totalPrice = payment.getPrice();
        int refundFee = totalPrice * refundPolicyOnDay.getRate() * (1 / 100);
        int refundPrice = totalPrice - refundFee;
        return PaymentInfo.builder()
                .refundFee(refundFee)
                .refundPrice(refundPrice)
                .price(totalPrice)
                .paymentMethod(payment.getMethod())
                .build();
    }

    private RefundPolicy getRefundPolicyOnDay(List<RefundPolicy> refundPolicies, int remainDate) {
        return refundPolicies.stream()
                .filter(refundPolicy -> refundPolicy.getRemaining().getRemain() == remainDate)
                .findFirst()
                .orElseThrow(() -> new BadRequestException(INVALID_RESERVATION_CANCEL_DATE));
    }

    private int getRemainDate(LocalDate reservationDate, LocalDate now) {
        int remainDate = reservationDate.getDayOfYear() - now.getDayOfYear();
        return remainDate > 8 ? 8 : remainDate;
    }

    public List<ReservationSettingsResponse> getAll(ReservationSettingsStatus tab, String studycafeName, LocalDate startDate, LocalDate endDate, Pageable pageable, String accessToken){
        initCondition(tab, startDate, endDate);
        Page<ReservationRecord> reservationRecordPage = reservationRecordRepository.getReservationRecordsConditions(tab, studycafeName, startDate, endDate, memberService.getMemberFromAccessToken(accessToken), pageable);
        return reservationRecordPage.getContent().stream().map(reservationRecord -> createReservationSettingsResponse(reservationRecord)).collect(Collectors.toList());
    }

    public ReservationSettingsResponse createReservationSettingsResponse(ReservationRecord reservationRecord) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        Payment payment = reservationRecord.getPayment();
        return ReservationSettingsResponse.builder()
                .studycafeName(studycafe.getName())
                .studycafePhoto(studycafe.getPhoto())
                .roomName(room.getName())
                .reservationDate(reservationRecord.getDate())
                .reservationStartTime(reservationRecord.getStartTime())
                .reservationEndTime(reservationRecord.getEndTime())
                .usingTime(reservationRecord.getDuration())
                .price(payment.getPrice())
                .paymentMethod(payment.getMethod())
                .cancelReason(payment.getCancelReason())
                .reservationStatus(getReservationSettingsResponse(reservationRecord.getStatus(), reservationRecord.getDate(), reservationRecord.getStartTime(), reservationRecord.getEndTime()).getStatusMessage())
                .build();
    }

    public ReservationSettingsStatus getReservationSettingsResponse(ReservationStatus reservationStatus, LocalDate reservationDate,
                                                                    LocalTime startTime, LocalTime endTime) {
        if (reservationStatus == ReservationStatus.CANCELED) return CANCELED;
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        if (reservationDate.isBefore(nowDate) || (reservationDate == nowDate && startTime.isAfter(nowTime))) return BEFORE_USING;
        if (reservationDate.isAfter(nowDate) || (reservationDate == nowDate && endTime.isBefore(nowTime))) return AFTER_USING;
        return USING;
    }

    private void initCondition(ReservationSettingsStatus tab, LocalDate startDate, LocalDate endDate) {
        if(tab == null) tab = ALL;
        if(startDate == null) startDate = LocalDate.now().minusYears(1);
        if(endDate == null) endDate = LocalDate.now();
    }
}
