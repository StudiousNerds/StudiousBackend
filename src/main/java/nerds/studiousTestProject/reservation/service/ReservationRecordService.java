package nerds.studiousTestProject.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.repository.ConvenienceRecordRepository;
import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.payment.entity.PaymentStatus;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.payment.util.PaymentGenerator;
import nerds.studiousTestProject.payment.util.fromtoss.Cancel;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import nerds.studiousTestProject.payment.util.totoss.CancelRequest;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.refundpolicy.repository.RefundPolicyRepository;
import nerds.studiousTestProject.reservation.controller.ViewCriteria;
import nerds.studiousTestProject.reservation.dto.admin.ShowAdminCancelResponse;
import nerds.studiousTestProject.reservation.dto.cancel.response.PaymentInfoWithRefund;
import nerds.studiousTestProject.reservation.dto.cancel.response.ReservationCancelResponse;
import nerds.studiousTestProject.reservation.dto.change.request.ChangeReservationRequest;
import nerds.studiousTestProject.convenience.dto.PaidConvenienceInfo;
import nerds.studiousTestProject.reservation.dto.change.response.ShowChangeReservationResponse;
import nerds.studiousTestProject.reservation.dto.detail.response.ReservationDetailResponse;
import nerds.studiousTestProject.reservation.dto.mypage.response.MypageReservationResponse;
import nerds.studiousTestProject.reservation.dto.reserve.request.PaidConvenience;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReserveRequest;
import nerds.studiousTestProject.reservation.dto.reserve.request.ReservationInfo;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationRecordInfoWithStatus;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaymentInfoResponse;
import nerds.studiousTestProject.reservation.dto.show.response.ReserveResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.DATE_ONLY_ONE_NULL;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_CHANGE_REQUEST;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_PAGE_NUMBER;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_PAGE_SIZE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_RESERVATION_CANCEL_DATE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_RESERVE_DATE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_USING_TIME;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISCALCULATED_PRICE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISCALCULATED_USING_TIME;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISMATCH_CANCEL_PRICE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.OVER_MAX_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.START_TIME_AFTER_THAN_END_TIME;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.USING_TIME_NOT_PER_HOUR;
import static nerds.studiousTestProject.payment.util.PaymentRequestStatus.CANCEL;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.AFTER_USING;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.BEFORE_USING;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.CANCELED;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.USING;

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
    private final PaymentGenerator paymentGenerator;

    private Map<Integer, Boolean> reservationTimes = new ConcurrentHashMap<>();
    private static final int RESERVATION_SETTINGS_PAGE_SIZE = 4;
    private static final int ADMIN_RESERVATION_SETTINGS_PAGE_SIZE = 10;
    private static final String ORDER_NAME_FORMAT = "%s 인원 %d명";
    private static final String CHANGE_CANCEL_REASON = "재결제를 위한 취소";


    @Transactional
    public PaymentInfoResponse reserve(final ReserveRequest reserveRequest, final Long roomId, final Long memberId) {
        final Room room = findRoomById(roomId);
        validReservationInfo(reserveRequest, room); // 운영시간 검증 필요 (공휴일 구현이 끝날 경우), 이미 예약 된 시간/날짜는 아닌지 확인
        final Payment payment = paymentRepository.save(createInProgressPayment(reserveRequest.getPrice()));
        final ReservationRecord reservationRecord = reservationRecordRepository.save(reserveRequest.toReservationRecord(room, findMemberById(memberId), payment));
        final String orderName = String.format(ORDER_NAME_FORMAT, room.getName(), reserveRequest.getReservation().getHeadCount());
        savePaidConvenienceRecord(reserveRequest, reservationRecord);
        return PaymentInfoResponse.of(payment, orderName);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }

    private Payment createInProgressPayment(final Integer price) {
        return Payment.builder()
                .status(PaymentStatus.IN_PROGRESS)
                .price(price)
                .orderId(UUID.randomUUID().toString())
                .build();
    }

    private void validReservationInfo(final ReserveRequest reserveRequest, final Room room) {
        ReservationInfo reservationInfo = reserveRequest.getReservation();
        validCorrectDate(reservationInfo);
        validCorrectTime(reservationInfo);
        validCalculateUsingTime(reservationInfo);
        validUsingTimePerHour(reservationInfo);
        validMinUsingTime(reservationInfo, room);
        validOverMaxHeadCount(reservationInfo.getHeadCount(), room);
        validCalculatePrice(reserveRequest, room);
    }

    private void validCalculatePrice(final ReserveRequest reserveRequest, final Room room) {
        ReservationInfo reservation = reserveRequest.getReservation();
        final int conveniencePrice = reserveRequest.getPaidConveniences().stream().mapToInt(PaidConvenience::getPrice).sum();
        if (room.getPriceType() == PriceType.PER_HOUR) {
            if (reserveRequest.getPrice() != room.getPrice() * reservation.getUsingTime() + conveniencePrice) {
                throw new BadRequestException(MISCALCULATED_PRICE);
            }
        }
        if (room.getPriceType() == PriceType.PER_PERSON) {
            int headCountToCalculate = Math.max(room.getMinHeadCount(), reservation.getHeadCount());
            if (reserveRequest.getPrice() != room.getPrice() * headCountToCalculate * reservation.getUsingTime() + conveniencePrice) {
                throw new BadRequestException(MISCALCULATED_PRICE);
            }
        }
    }

    private void validCorrectDate(final ReservationInfo reservationInfo) {
        if (reservationInfo.getDate().isBefore(LocalDate.now())) { // 예약 날짜가 오늘 전일 경우 (지난 날짜일 경우)
            throw new BadRequestException(INVALID_RESERVE_DATE);
        }
    }

    private void validOverMaxHeadCount(final int headCount, Room room) {
        if (room.getMaxHeadCount() < headCount) throw new BadRequestException(OVER_MAX_HEADCOUNT);
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

    private void savePaidConvenienceRecord(final ReserveRequest reserveRequest,final  ReservationRecord reservationRecord) {
        reserveRequest.getPaidConveniences().stream()
                .forEach(paidConvenience -> convenienceRecordRepository.save(paidConvenience.toConvenienceRecord(reservationRecord)));
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

    public ReservationCancelResponse getCancelInfo(final Long reservationId) {
        final ReservationRecord reservationRecord = findByIdWithPaymentAndPlace(reservationId);
        final Studycafe studycafe = reservationRecord.getRoom().getStudycafe();
        final List<RefundPolicy> refundPolicies = refundPolicyRepository.findAllByStudycafe(studycafe);

        final int remainDate = getRemainDate(reservationRecord.getDate(), LocalDate.now());
        final RefundPolicy refundPolicyOnDay = getRefundPolicyOnDay(refundPolicies, remainDate);
        PaymentInfoWithRefund paymentInfoWithRefund = calculateRefundMoney(reservationRecord.getPayment(), refundPolicyOnDay);
        return ReservationCancelResponse.of(reservationRecord, paymentInfoWithRefund, refundPolicies, refundPolicyOnDay);
    }

    private ReservationRecord findByIdWithPaymentAndPlace(Long reservationRecordId) {
        return reservationRecordRepository.findByIdWithPaymentAndPlace(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    private PaymentInfoWithRefund calculateRefundMoney(final Payment payment, final RefundPolicy refundPolicyOnDay) {
        final int price = payment.getPrice();
        final int refundPrice = price * refundPolicyOnDay.getRate() * (1 / 100);
        final int refundFee = price - refundPrice;
        return PaymentInfoWithRefund.of(refundPrice, refundFee, payment);
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

    public MypageReservationResponse getAll(final ReservationSettingsStatus tab, final String studycafeName, LocalDate startDate, LocalDate endDate, final Pageable pageable, final Long memberId) {
        validPageable(pageable);
        final Member member = findMemberById(memberId);
        final Page<ReservationRecord> reservationRecordPage = reservationRecordRepository.getReservationRecordsConditions(tab, studycafeName, startDate, endDate, member, pageable);
        final List<ReservationRecordInfoWithStatus> reservationRecordInfoWithStatusList = reservationRecordPage.getContent().stream().map(reservationRecord -> createReservationSettingsResponse(reservationRecord)).collect(Collectors.toList());
        return MypageReservationResponse.of(reservationRecordInfoWithStatusList, reservationRecordPage);
    }

    private void validPageable(Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new BadRequestException(INVALID_PAGE_NUMBER);
        }
        if (pageable.getPageSize() < 1) {
            throw new BadRequestException(INVALID_PAGE_SIZE);
        }
    }

    private int validPageAndAssign(Integer page) {
        if (page < 1 || page == null) throw new BadRequestException(INVALID_PAGE_NUMBER);
        return page - 1;
    }

    public ReservationRecordInfoWithStatus createReservationSettingsResponse(final ReservationRecord reservationRecord) {
        final Payment payment = reservationRecord.getPayment();
        final ReservationSettingsStatus status = getReservationSettingsResponse(reservationRecord);
        return ReservationRecordInfoWithStatus.of(reservationRecord, payment, status);
    }

    private ReservationSettingsStatus getReservationSettingsResponse(final ReservationRecord reservationRecord) {
        final LocalDate reserveDate = reservationRecord.getDate();
        if (reservationRecord.getStatus() == ReservationStatus.CANCELED) return CANCELED;
        final LocalDate nowDate = LocalDate.now();
        final LocalTime nowTime = LocalTime.now();
        if (reserveDate.isBefore(nowDate) || (reserveDate == nowDate && reservationRecord.getStartTime().isAfter(nowTime)))
            return AFTER_USING;
        if (reserveDate.isAfter(nowDate) || (reserveDate == nowDate && reservationRecord.getEndTime().isBefore(nowTime)))
            return BEFORE_USING;
        return USING;
    }

    public ReservationDetailResponse showDetail(final Long reservationRecordId) {
        final ReservationRecord reservationRecord = findByIdWithPaymentAndPlace(reservationRecordId);
        return ReservationDetailResponse.of(reservationRecord);
    }

    public ShowChangeReservationResponse showChangeReservation(final Long reservationRecordId) {
        final ReservationRecord reservationRecord = reservationRecordRepository.findByIdWithPlace(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
        final List<Convenience> paidConvenienceList = convenienceRepository.findAllByRoomIdWherePaid(reservationRecord.getRoom().getId());
        final List<PaidConvenienceInfo> paidConvenienceListPaid = convenienceRecordRepository.findAllByReservationRecord(reservationRecord).stream()
                .map(PaidConvenienceInfo::from).toList();
        final List<PaidConvenienceInfo> paidConvenienceListNotPaid = paidConvenienceList.stream()
                .filter(convenience -> !paidConvenienceListPaid.contains(convenience.getName().name())).map(PaidConvenienceInfo::from).toList();
        return ShowChangeReservationResponse.of(reservationRecord, paidConvenienceListPaid, paidConvenienceListNotPaid);
    }


    @Transactional
    public PaymentInfoResponse change(final Long reservationRecordId, final ChangeReservationRequest request) {
        validRequestBothNull(request);
        ReservationRecord reservationRecord = findByIdWithPaymentAndPlace(reservationRecordId);
        int price = 0;
        final Room room = reservationRecord.getRoom();
        price += updateHeadCount(reservationRecord, request.getHeadCount(), room);
        if (request.getPrice() == 0 && price == 0 && request.getConveniences() == null) {
            return null;
        }
        final int previousPrice = cancelPreviousPayment(request, reservationRecord);
        final Payment payment = paymentRepository.save(createInProgressPayment(previousPrice + request.getPrice()));
        reservationRecord.updatePayment(payment);
        price += updateConvenienceRecord(reservationRecord, request.getConveniences());
        validMatchPrice(request, price);
        final String orderName = String.format(ORDER_NAME_FORMAT, room.getName(), reservationRecord.getHeadCount());
        return PaymentInfoResponse.of(payment, orderName);
    }

    private int cancelPreviousPayment(final ChangeReservationRequest request, final ReservationRecord reservationRecord) {
        Payment payment = reservationRecord.getPayment();
        final Integer price = payment.getPrice();
        final PaymentResponseFromToss responseFromToss = paymentGenerator.requestToToss(CANCEL.getUriFormat(payment.getPaymentKey()), CancelRequest.from(CHANGE_CANCEL_REASON, request));
        validCancelPrice(payment, responseFromToss);
        paymentRepository.delete(payment);
        return price;
    }

    private void validCancelPrice(final Payment previousPayment, final PaymentResponseFromToss responseFromToss) {
        if (responseFromToss.getCancels().stream().mapToInt(Cancel::getCancelAmount).sum() != previousPayment.getPrice()) {
            throw new BadRequestException(MISMATCH_CANCEL_PRICE);
        }
    }

    private int updateConvenienceRecord(final ReservationRecord reservationRecord, final List<PaidConvenienceInfo> conveniences) {
        int price = 0;
        if (conveniences != null) {
            for (PaidConvenienceInfo convenience : conveniences) {
                price += convenience.getPrice();
                convenienceRecordRepository.save(convenience.toConvenienceRecord(reservationRecord));
            }
        }
        return price;
    }

    private int updateHeadCount(final ReservationRecord reservationRecord, final Integer headCount, final Room room) {
        int price = 0;
        if (headCount != null) {
            validOverMaxHeadCount(headCount, room);
            if (room.getPriceType() == PriceType.PER_PERSON) {
                price += reservationRecord.getUsingTime() * (headCount - reservationRecord.getHeadCount());
            }
            reservationRecord.updateHeadCount(headCount);
        }
        return price;
    }

    private void validMatchPrice(final ChangeReservationRequest request, final int price) {
        if (price != request.getPrice()) {
            throw new BadRequestException(MISCALCULATED_PRICE);
        }
    }

    private void validRequestBothNull(final ChangeReservationRequest request) {
        if (request.isBothNull())
            throw new BadRequestException(INVALID_CHANGE_REQUEST);
    }

    public ShowAdminCancelResponse showAdminCancel(final Long reservationId) {
        final ReservationRecord reservationRecord = findByIdWithPlace(reservationId);
        return ShowAdminCancelResponse.from(reservationRecord);
    }

    public void showAdmin(final ViewCriteria viewCriteria, LocalDate startDate, LocalDate endDate, final ReservationSettingsStatus reservationStatus, final Long studycafeId, final Long roomId, Integer page) {
        int pageNumber = validPageAndAssign(page);
        assignRecentOneYearIfBothNull(startDate, endDate);
        validDateOnlyOneNull(startDate, endDate);

    }

    private void validDateOnlyOneNull(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException(DATE_ONLY_ONE_NULL);
        }
    }

    private void assignRecentOneYearIfBothNull(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            startDate = LocalDate.now().minusYears(1);
            endDate = LocalDate.now();
        }
    }
}
