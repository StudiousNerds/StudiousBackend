package nerds.studiousTestProject.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceRecord;
import nerds.studiousTestProject.convenience.repository.ConvenienceRecordRepository;
import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.payment.entity.PaymentStatus;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.refundpolicy.repository.RefundPolicyRepository;
import nerds.studiousTestProject.reservation.dto.PaymentInfo;
import nerds.studiousTestProject.reservation.dto.cancel.response.PaymentInfoWithRefund;
import nerds.studiousTestProject.reservation.dto.cancel.response.RefundPolicyInfoWithOnDay;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationRecordInfoWithPlace;
import nerds.studiousTestProject.reservation.dto.change.request.ChangeReservationRequest;
import nerds.studiousTestProject.reservation.dto.change.response.PaidConvenienceInfo;
import nerds.studiousTestProject.reservation.dto.change.response.ShowChangeReservationResponse;
import nerds.studiousTestProject.reservation.dto.detail.response.ReservationDetailResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.MypageReservationResponse;
import nerds.studiousTestProject.reservation.dto.reserve.request.PaidConvenience;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReserveRequest;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReservationInfo;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationRecordInfoWithStatus;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaymentInfoResponse;
import nerds.studiousTestProject.reservation.dto.show.response.ReserveResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.PriceType;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_CHANGE_REQUEST;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_PAGE_NUMBER;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_RESERVATION_CANCEL_DATE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_RESERVE_DATE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_USING_TIME;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISCALCULATED_PRICE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISCALCULATED_USING_TIME;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISMATCH_PRICE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_PAYMENT;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.OVER_MAX_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.START_TIME_AFTER_THAN_END_TIME;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.USING_TIME_NOT_PER_HOUR;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class ReservationRecordService {

    private final MemberRepository memberRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomRepository roomRepository;
    private final StudycafeRepository studycafeRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final PaymentRepository paymentRepository;
    private final ConvenienceRepository convenienceRepository;
    private final ConvenienceRecordRepository convenienceRecordRepository;

    private Map<Integer, Boolean> reservationTimes = new ConcurrentHashMap<>();
    private static final int RESERVATION_SETTINGS_PAGE_SIZE = 4;
    private static final String ORDER_NAME_FORMAT = "%s 인원 %d명";

    @Transactional
    public PaymentInfoResponse reserve(final ReserveRequest reserveRequest, final Long roomId, final Long memberId) {
        final Room room = findRoomById(roomId);
        validReservationInfo(reserveRequest, room); // 운영시간 검증 필요 (공휴일 구현이 끝날 경우), 이미 예약 된 시간/날짜는 아닌지 확인
        final ReservationRecord reservationRecord = reservationRecordRepository.save(reserveRequest.toReservationRecord(room, findMemberById(memberId)));
        final Payment payment = paymentRepository.save(createInProgressPayment(reservationRecord, reserveRequest.getReservationInfo().getPrice()));
        final String orderName = String.format(ORDER_NAME_FORMAT, room.getName(), reserveRequest.getReservationInfo().getHeadCount());
        savePaidConvenienceRecord(reserveRequest, reservationRecord, payment);
        return PaymentInfoResponse.of(payment, orderName);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }

    private Payment createInProgressPayment(final ReservationRecord reservationRecord, final Integer price) {
        return Payment.builder()
                .reservationRecord(reservationRecord)
                .status(PaymentStatus.IN_PROGRESS)
                .price(price)
                .orderId(UUID.randomUUID().toString())
                .build();
    }

    private void validReservationInfo(final ReserveRequest reserveRequest, final Room room) {
        ReservationInfo reservationInfo = reserveRequest.getReservationInfo();
        validCorrectDate(reservationInfo);
        validCorrectTime(reservationInfo);
        validCalculateUsingTime(reservationInfo);
        validUsingTimePerHour(reservationInfo);
        validMinUsingTime(reservationInfo, room);
        validOverMaxHeadCount(reservationInfo, room);
        validCalculatePrice(reserveRequest, room, reservationInfo);
    }

    private void validCalculatePrice(final ReserveRequest reserveRequest, final Room room, final ReservationInfo reservationInfo) {
        final int conveniencePrice = reserveRequest.getPaidConveniences().stream().mapToInt(PaidConvenience::getPrice).sum();
        if (room.getPriceType() == PriceType.PER_HOUR) {
            if (reservationInfo.getPrice() != room.getPrice() * reservationInfo.getUsingTime() + conveniencePrice) {
                throw new BadRequestException(MISCALCULATED_PRICE);
            }
        }
        if (room.getPriceType() == PriceType.PER_PERSON) {
            int headCountToCalculate = Math.max(room.getMinHeadCount(), reservationInfo.getHeadCount());
            if (reservationInfo.getPrice() != room.getPrice() * headCountToCalculate * reservationInfo.getUsingTime() + conveniencePrice) {
                throw new BadRequestException(MISCALCULATED_PRICE);
            }
        }
    }

    private void validCorrectDate(final ReservationInfo reservationInfo) {
        if (reservationInfo.getDate().isBefore(LocalDate.now())) { // 예약 날짜가 오늘 전일 경우 (지난 날짜일 경우)
            throw new BadRequestException(INVALID_RESERVE_DATE);
        }
    }

    private void validOverMaxHeadCount(final ReservationInfo reservationInfo, Room room) {
        if (room.getMaxHeadCount() < reservationInfo.getHeadCount()) throw new BadRequestException(OVER_MAX_HEADCOUNT);
    }

    private void validCorrectTime(final ReservationInfo reservationInfo) {
        if (reservationInfo.getStartTime().isAfter(reservationInfo.getEndTime())) { // 예약 끝 시간은 시작 시간보다 뒤여야함
            throw new BadRequestException(START_TIME_AFTER_THAN_END_TIME);
        }
    }

    private void validUsingTimePerHour(final ReservationInfo reservationInfo) {
        if (reservationInfo.getStartTime().getMinute() != 0 || reservationInfo.getEndTime().getMinute() != 0) { //시간 단위가 아닐 때
            throw new BadRequestException(USING_TIME_NOT_PER_HOUR);
        }
    }

    private void validCalculateUsingTime(final ReservationInfo reservationInfo) {
        if (reservationInfo.getEndTime().getHour() - reservationInfo.getStartTime().getHour() != reservationInfo.getUsingTime()) {
            throw new BadRequestException(MISCALCULATED_USING_TIME);
        }
    }

    private void validMinUsingTime(final ReservationInfo reservationInfo,final  Room room) {
        if (reservationInfo.getUsingTime() < room.getMinUsingTime()) { // 최소 이용시간 보다 작을 때
            throw new BadRequestException(INVALID_USING_TIME);
        }
    }

    private void savePaidConvenienceRecord(final ReserveRequest reserveRequest,final  ReservationRecord reservationRecord,final  Payment payment) {
        reserveRequest.getPaidConveniences().stream()
                .forEach(paidConvenience -> convenienceRecordRepository.save(paidConvenience.toConvenienceRecord(reservationRecord, payment)));
    }

    public Map<Integer, Boolean> getReservationTimes(LocalDate date, Long studycafeId, Long roomId) {
        Studycafe studycafe = studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        LocalTime openTime = studycafe.getOperationInfos().get(0).getStartTime() != null ? studycafe.getOperationInfos().get(0).getStartTime() : LocalTime.MIN;
        LocalTime endTime = studycafe.getOperationInfos().get(1).getEndTime() != null ? studycafe.getOperationInfos().get(1).getEndTime() : LocalTime.MAX;

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
        Integer minUsingTime = room.getMinUsingTime();

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

    public ReservationRecord findByIdWithPlace(final Long reservationRecordId) {
        return reservationRecordRepository.findByIdWithPlace(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    public Room findRoomById(final Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
    }

    private Room findRoomByIdWithStudycafe(final Long roomId) {
        return roomRepository.findByIdWithStudycafe(roomId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
    }

    public ReserveResponse show(final Long roomId, final Long memberId) {
        final Member member = findMemberById(memberId);
        final Room room = findRoomByIdWithStudycafe(roomId);
        final Studycafe studycafe = room.getStudycafe();
        final List<Convenience> conveniences = convenienceRepository.findAllByRoom(room);
        final List<RefundPolicy> refundPolicyList = refundPolicyRepository.findAllByStudycafe(studycafe);
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

    public ReservationCancelResponse getCancelInfo(final Long reservationId) {
        final ReservationRecord reservationRecord = findByIdWithPlace(reservationId);
        final Room room = reservationRecord.getRoom();
        final Studycafe studycafe = room.getStudycafe();
        final List<RefundPolicy> refundPolicies = refundPolicyRepository.findAllByStudycafe(studycafe);
        final List<Payment> payments = findPaymentsByReservationRecord(reservationRecord);

        final int remainDate = getRemainDate(reservationRecord.getDate(), LocalDate.now());
        final RefundPolicy refundPolicyOnDay = getRefundPolicyOnDay(refundPolicies, remainDate);

        return ReservationCancelResponse.builder()
                .reservation(ReservationRecordInfoWithPlace.of(studycafe, room, reservationRecord))
                .paymentRecord(calculateRefundMoney(payments, refundPolicyOnDay))
                .refundPolicy(RefundPolicyInfoWithOnDay.of(refundPolicies, refundPolicyOnDay))
                .build();
    }

    private List<Payment> findPaymentsByReservationRecord(final ReservationRecord reservationRecord) {
        return paymentRepository.findAllByReservationRecord(reservationRecord);
    }

    private PaymentInfoWithRefund calculateRefundMoney(final List<Payment> payments, final RefundPolicy refundPolicyOnDay) {
        final int totalPrice = payments.stream().mapToInt(Payment::getPrice).sum();
        final int refundPrice = totalPrice * refundPolicyOnDay.getRate() * (1 / 100);
        final int refundFee = totalPrice - refundPrice;
        return new PaymentInfoWithRefund(totalPrice, refundPrice, refundFee, payments.stream().map(PaymentInfo::from).toList());
    }

    private RefundPolicy getRefundPolicyOnDay(final List<RefundPolicy> refundPolicies, final int remainDate) {
        return refundPolicies.stream()
                .filter(refundPolicy -> refundPolicy.getRemaining().getRemain() == remainDate)
                .findFirst()
                .orElseThrow(() -> new BadRequestException(INVALID_RESERVATION_CANCEL_DATE));
    }

    private int getRemainDate(final LocalDate reservationDate, final LocalDate now) {
        int remainDate = reservationDate.getDayOfYear() - now.getDayOfYear();
        return remainDate > 8 ? 8 : remainDate;
    }

    public MypageReservationResponse getAll(ReservationSettingsStatus tab, String studycafeName, LocalDate startDate, LocalDate endDate, int page, Long memberId) {
        page = validPageAndAssign(page);
        final Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));
        final Page<ReservationRecord> reservationRecordPage = reservationRecordRepository.getReservationRecordsConditions(tab, studycafeName, startDate, endDate, member, PageRequest.of(page, RESERVATION_SETTINGS_PAGE_SIZE));
        final List<ReservationRecordInfoWithStatus> reservationRecordInfoWithStatusList = reservationRecordPage.getContent().stream().map(reservationRecord -> createReservationSettingsResponse(reservationRecord)).collect(Collectors.toList());
        return MypageReservationResponse.of(reservationRecordInfoWithStatusList, reservationRecordPage);
    }

    private int validPageAndAssign(Integer page) {
        if (page < 1 || page == null) throw new BadRequestException(INVALID_PAGE_NUMBER);
        return page - 1;
    }

    public ReservationRecordInfoWithStatus createReservationSettingsResponse(final ReservationRecord reservationRecord) {
        final Room room = reservationRecord.getRoom();
        final Studycafe studycafe = room.getStudycafe();
        final Payment payment = findPaymentByReservation(reservationRecord);
        return ReservationRecordInfoWithStatus.of(studycafe, room, reservationRecord, payment);
    }

    public ReservationDetailResponse showDetail(final Long reservationRecordId) {
        final ReservationRecord reservationRecord = findReservationByIdWithPlace(reservationRecordId);
        final Payment payment = findPaymentByReservation(reservationRecord);
        return ReservationDetailResponse.of(reservationRecord, payment);
    }

    private ReservationRecord findReservationByIdWithPlace(final Long reservationRecordId) {
        return reservationRecordRepository.findByIdWithPlace(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    private Payment findPaymentByReservation(final ReservationRecord reservationRecord) {
        return paymentRepository.findByReservationRecord(reservationRecord)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_PAYMENT));
    }

    public ShowChangeReservationResponse showChangeReservation(final Long reservationRecordId) {
        final ReservationRecord reservationRecord = reservationRecordRepository.findByIdWithPlace(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
        final int price = paymentRepository.findTotalPriceByReservationId(reservationRecord);
        final List<Convenience> paidConvenienceList = convenienceRepository.findAllByRoomIdWherePaid(reservationRecord.getRoom().getId());
        final List<PaidConvenienceInfo> paidConvenienceListPaid = convenienceRecordRepository.findAllByReservationRecord(reservationRecord).stream()
                .map(PaidConvenienceInfo::from).toList();
        final List<PaidConvenienceInfo> paidConvenienceListNotPaid = paidConvenienceList.stream()
                .filter(convenience -> !paidConvenienceListPaid.contains(convenience.getName().name())).map(PaidConvenienceInfo::from).toList();
        return ShowChangeReservationResponse.of(reservationRecord, price, paidConvenienceListPaid, paidConvenienceListNotPaid);
    }


    @Transactional
    public void change(final Long reservationRecordId, final ChangeReservationRequest request) {
        ReservationRecord reservationRecord = findByIdWithPlace(reservationRecordId);
        Payment payment = paymentRepository.save(createInProgressPayment(reservationRecord, request.getPrice()));
        final Integer headCount = request.getHeadCount();
        List<PaidConvenienceInfo> conveniences = request.getConveniences();
        if(conveniences == null && headCount == null)
            throw new BadRequestException(INVALID_CHANGE_REQUEST);
        int price = 0;
        Room room = reservationRecord.getRoom();
        if (headCount != null) {
            if (request.getHeadCount() > room.getMaxHeadCount()) {
                throw new BadRequestException(OVER_MAX_HEADCOUNT);
            }
            if (room.getPriceType() == PriceType.PER_PERSON) {
                price += reservationRecord.getUsingTime() * (headCount - reservationRecord.getHeadCount());
            }
            reservationRecord.updateHeadCount(headCount);
        }


        if (conveniences != null) {
            for (PaidConvenienceInfo convenience : conveniences) {
                price += convenience.getPrice();
                convenienceRecordRepository.save(convenience.toConvenienceRecord(reservationRecord, payment));
            }
        }

        if (price != request.getPrice()) {
            throw new BadRequestException(MISMATCH_PRICE);
        }

    }
}
