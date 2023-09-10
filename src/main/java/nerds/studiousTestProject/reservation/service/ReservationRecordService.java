package nerds.studiousTestProject.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceRecord;
import nerds.studiousTestProject.convenience.repository.ConvenienceRecordRepository;
import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.refundpolicy.repository.RefundPolicyRepository;
import nerds.studiousTestProject.reservation.dto.cancel.response.RefundPolicyInfoWithOnDay;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationRecordInfo;
import nerds.studiousTestProject.reservation.dto.detail.response.ReservationDetailResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.MypageReservationResponse;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReserveRequest;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReservationInfo;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.cancel.response.PaymentInfo;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationRecordInfoWithStatus;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaymentInfoResponse;
import nerds.studiousTestProject.reservation.dto.show.response.ReserveResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_PAGE_NUMBER;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_USING_TIME;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_RESERVATION_CANCEL_DATE;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_RESERVE_DATE;
import static nerds.studiousTestProject.common.exception.ErrorCode.MISCALCULATED_USING_TIME;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.ErrorCode.START_TIME_AFTER_THAN_END_TIME;
import static nerds.studiousTestProject.common.exception.ErrorCode.USING_TIME_NOT_PER_HOUR;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class ReservationRecordService {

    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomRepository roomRepository;
    private final StudycafeRepository studycafeRepository;
    private final TokenService tokenService;
    private final RefundPolicyRepository refundPolicyRepository;
    private Map<Integer, Boolean> reservationTimes = new ConcurrentHashMap<>();

    private final ConvenienceRepository convenienceRepository;
    private final ConvenienceRecordRepository convenienceRecordRepository;
    private static final int RESERVATION_SETTINGS_PAGE_SIZE = 4;

    @Transactional
    public PaymentInfoResponse reserve(ReserveRequest reserveRequest, Long roomId, String accessToken) {
        Room room = findRoomById(roomId);
        validReservationInfo(reserveRequest.getReservationInfo(), room); // 운영시간 검증 필요 (공휴일 구현이 끝날 경우)
        ReservationRecord reservationRecord = reservationRecordRepository.save(reserveRequest.toReservationRecord(room, tokenService.getMemberFromAccessToken(accessToken)));
        savePaidConvenienceRecord(reserveRequest, reservationRecord);
        return PaymentInfoResponse.of(reserveRequest, reservationRecord);
    }

    private void validReservationInfo(ReservationInfo reservationInfo, Room room) {
        validCorrectDate(reservationInfo);
        validCorrectTime(reservationInfo);
        validCalculateUsingTime(reservationInfo);
        validUsingTimePerHour(reservationInfo);
        validMinUsingTime(reservationInfo, room);
    }

    private void validCorrectDate(ReservationInfo reservationInfo) {
        if (reservationInfo.getDate().isBefore(LocalDate.now())) { // 예약 날짜가 오늘 전일 경우 (지난 날짜일 경우)
            throw new BadRequestException(INVALID_RESERVE_DATE);
        }
    }

    private void validCorrectTime(ReservationInfo reservationInfo) {
        if (reservationInfo.getStartTime().isAfter(reservationInfo.getEndTime())) { // 예약 끝 시간은 시작 시간보다 뒤여야함
            throw new BadRequestException(START_TIME_AFTER_THAN_END_TIME);
        }
    }

    private void validUsingTimePerHour(ReservationInfo reservationInfo) {
        if (reservationInfo.getStartTime().getMinute() != 0 || reservationInfo.getEndTime().getMinute() != 0) { //시간 단위가 아닐 때
            throw new BadRequestException(USING_TIME_NOT_PER_HOUR);
        }
    }

    private void validCalculateUsingTime(ReservationInfo reservationInfo) {
        if (reservationInfo.getEndTime().getHour() - reservationInfo.getStartTime().getHour() != reservationInfo.getUsingTime()) {
            throw new BadRequestException(MISCALCULATED_USING_TIME);
        }
    }

    private void validMinUsingTime(ReservationInfo reservationInfo, Room room) {
        if (reservationInfo.getUsingTime() < room.getMinUsingTime()){ // 최소 이용시간 보다 작을 때
            throw new BadRequestException(INVALID_USING_TIME);
        }
    }

    private void savePaidConvenienceRecord(ReserveRequest reserveRequest, ReservationRecord reservationRecord) {
        reserveRequest.getPaidConveniences().stream().map(paidConvenience -> convenienceRecordRepository.save(ConvenienceRecord.builder()
                .reservationRecord(reservationRecord)
                .convenienceName(paidConvenience.getConvenienceName())
                .price(paidConvenience.getPrice())
                .build()));
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

    public ReserveResponse show(Long cafeId, Long roomId, String accessToken) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Room room = findRoomById(roomId);
        Studycafe studycafe = findStudycafeById(cafeId);
        List<Convenience> conveniences = convenienceRepository.findAllByRoom(room);
        List<RefundPolicy> refundPolicyList = refundPolicyRepository.findAllByStudycafe(studycafe);
        return ReserveResponse.of(member, room, studycafe, conveniences, refundPolicyList);
    }

    public Page<ReservationRecord> findAllByMemberId(Long memberId, Pageable pageable) {
        return reservationRecordRepository.findAllByMemberId(memberId, pageable);
    }

    public List<ReservationRecord> findAllByRoomId(Long roomId) {
        return reservationRecordRepository.findAllByRoomId(roomId);
    }

    public List<ReservationRecord> findAllByStudycafeId(Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }

    public ReservationRecord findByReviewId(Long reviewId) {
        return reservationRecordRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    public ReservationCancelResponse getCancelInfo(Long reservationId) {
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
                .refundPolicyInfo(RefundPolicyInfoWithOnDay.of(refundPolicies, refundPolicyOnDay))
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

    public MypageReservationResponse getAll(ReservationSettingsStatus tab, String studycafeName, LocalDate startDate, LocalDate endDate, int page, String accessToken){
        page = validPageAndAssign(page);
        Page<ReservationRecord> reservationRecordPage = reservationRecordRepository.getReservationRecordsConditions(tab, studycafeName, startDate, endDate, tokenService.getMemberFromAccessToken(accessToken), PageRequest.of(page, RESERVATION_SETTINGS_PAGE_SIZE));
        List<ReservationRecordInfoWithStatus> reservationRecordInfoWithStatusList = reservationRecordPage.getContent().stream().map(reservationRecord -> createReservationSettingsResponse(reservationRecord)).collect(Collectors.toList());
        return MypageReservationResponse.of(reservationRecordInfoWithStatusList, reservationRecordPage);
    }

    private int validPageAndAssign(Integer page) {
        if(page < 1 || page == null) throw new BadRequestException(INVALID_PAGE_NUMBER);
        return page - 1;
    }

    public ReservationRecordInfoWithStatus createReservationSettingsResponse(ReservationRecord reservationRecord) {
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        Payment payment = reservationRecord.getPayment();
        return ReservationRecordInfoWithStatus.of(studycafe, room, reservationRecord, payment);
    }

    public ReservationDetailResponse showDetail(Long reservationRecordId) {
        ReservationRecord reservationRecord = findById(reservationRecordId);
        Room room = reservationRecord.getRoom();
        Studycafe studycafe = room.getStudycafe();
        Payment payment = reservationRecord.getPayment();
        return ReservationDetailResponse.of(reservationRecord, studycafe, room, payment);
    }


}
