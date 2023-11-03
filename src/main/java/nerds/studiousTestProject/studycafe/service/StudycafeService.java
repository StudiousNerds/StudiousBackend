package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.HolidayProvider;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyInfo;
import nerds.studiousTestProject.reservation.dto.ReservedTimeInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowOperationInfoInResponse;
import nerds.studiousTestProject.room.dto.show.ShowRoomResponse;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchSortType;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchInResponse;
import nerds.studiousTestProject.studycafe.entity.Notice;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Week;
import nerds.studiousTestProject.studycafe.repository.OperationInfoRepository;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class StudycafeService {
    private final HolidayProvider holidayProvider;
    private final OperationInfoRepository operationInfoRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    private final StudycafeRepository studycafeRepository;

    private static final String SHOW_STUDYCAFE_HASHTAGS_SEPARATOR = ",";
    private static final double INITIAL_GRADE = 0.;
    private static final int MAX_HOUR = 24;

    public List<SearchResponse> enquire(final String keyword,
                                        final LocalDate date,
                                        final LocalTime startTime,
                                        final LocalTime endTime,
                                        final Integer headCount,
                                        final Integer minGrade,
                                        final List<HashtagName> hashtags,
                                        final List<ConvenienceName> conveniences,
                                        final SearchSortType sortType,
                                        final Pageable pageable,
                                        final Long memberId) {
        final Week week = createWeekByDateConsiderHoliday(date);

        final SearchRequest searchRequest = SearchRequest.builder()
                .memberId(memberId)
                .keyword(keyword)
                .date(date)
                .week(week)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(headCount)
                .minGrade(minGrade)
                .hashtags(hashtags)
                .conveniences(conveniences)
                .sortType(sortType)
                .build();

        final List<SearchInResponse> searchInResponses = studycafeRepository.getSearchResult(searchRequest, pageable).getContent();
        return searchInResponses.stream().map(s -> SearchResponse.from(
                s, parseStringToHashtagNames(s.getAccumHashtags()), getGradeFromSumAndCount(s.getGradeSum(), s.getGradeCount()))
        ).toList();
    }

    private List<HashtagName> parseStringToHashtagNames(final String accumHashtags) {
        return Arrays.stream(accumHashtags.split(SHOW_STUDYCAFE_HASHTAGS_SEPARATOR)).map(HashtagName::valueOf).toList();
    }

    private double getGradeFromSumAndCount(final Double gradeSum, final Integer gradeCount) {
        if (gradeSum == null || gradeCount == null || gradeCount == 0) {
            return INITIAL_GRADE;
        }

        return gradeSum / gradeCount;
    }

    public ShowDetailsResponse findStudycafeByDate(final Long studycafeId, final LocalDate today) {
        final Studycafe studycafe = findStudycafeById(studycafeId);
        final List<Room> rooms = studycafe.getRooms();
        final List<ShowRoomResponse> showRoomResponses = parseRoomsToEnquireRoomResponses(studycafeId, today, rooms);
        return ShowDetailsResponse.of(studycafe, getPhotos(studycafe), showRoomResponses);
    }

    private List<ShowRoomResponse> parseRoomsToEnquireRoomResponses(final Long studycafeId,
                                                                    final LocalDate today,
                                                                    final List<Room> rooms) {
        return rooms.stream()
                .map(r -> ShowRoomResponse.of(r, getCanReserveDateTime(today, r.getMinUsingTime(), r.getId(), studycafeId)))
                .toList();
    }

    public List<RefundPolicyInfo> findRefundPolicies(final Long studycafeId) {
        final Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getRefundPolicies().stream()
                .map(RefundPolicyInfo::from)
                .collect(Collectors.toList());
    }

    public List<String> findNotices(final Long studycafeId) {
        final Studycafe studycafe = findStudycafeById(studycafeId);
        return studycafe.getNotices().stream().map(Notice::getDetail).toList();
    }

    private Studycafe findStudycafeById(final Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private List<String> getPhotos(final Studycafe studycafe) {
        final List<String> photos = new ArrayList<>();
        photos.add(studycafe.getPhoto());

        final List<String> subPhotos = studycafe.getSubPhotos().stream().map(SubPhoto::getPath).toList();
        photos.addAll(subPhotos);

        return photos;
    }

    private Map<LocalDate, List<Integer>> getCanReserveDateTime(final LocalDate today,
                                                                final int minUsingTime,
                                                                final Long roomId,
                                                                final Long studycafeId) {
        final Map<LocalDate, List<ReservedTimeInfo>> reservedTimesGroupingByDate = getReservedTimesGroupingByDate(roomId, today);
        final Map<Week, ShowOperationInfoInResponse> operationInfoGroupingByWeek = getOperationInfosGroupingByWeek(studycafeId);
        return getCanReserveTimesGroupingByDate(today, minUsingTime, operationInfoGroupingByWeek, reservedTimesGroupingByDate);
    }

    private Map<LocalDate, List<ReservedTimeInfo>> getReservedTimesGroupingByDate(final Long roomId, final LocalDate today) {
        final List<ReservationRecord> reservationRecords = reservationRecordRepository.findAllByFutureReservedTimeGroupingByDate(today, roomId);
        return reservationRecords.stream()
                .collect(Collectors.groupingBy(ReservationRecord::getDate, Collectors.mapping(ReservedTimeInfo::of, Collectors.toList())));
    }

    private Map<Week, ShowOperationInfoInResponse> getOperationInfosGroupingByWeek(final Long studycafeId) {
        final Map<Week, ShowOperationInfoInResponse> operationInfoGroupingByWeek = new ConcurrentHashMap<>();
        final List<OperationInfo> operationInfos = operationInfoRepository.findAllByStudycafeId(studycafeId);
        operationInfos.forEach(o -> operationInfoGroupingByWeek.put(o.getWeek(), ShowOperationInfoInResponse.of(o)));
        return operationInfoGroupingByWeek;
    }

    private Map<LocalDate, List<Integer>> getCanReserveTimesGroupingByDate(final LocalDate today,
                                                                           final int minUsingTime,
                                                                           final Map<Week, ShowOperationInfoInResponse> operationInfoGroupingByWeek,
                                                                           final Map<LocalDate, List<ReservedTimeInfo>> reservedTimesGroupingByDate) {
        final Map<LocalDate, List<Integer>> canReserveTimesGroupingByDate = new ConcurrentHashMap<>();
        final int todayDate = today.getDayOfMonth();
        final int currentMonthLastDay = today.getMonth().maxLength();
        for (int i = todayDate; i <= currentMonthLastDay; i++) {
            final LocalDate currentDate = today.plusDays(i - todayDate);
            final ShowOperationInfoInResponse currentOperationInfo = operationInfoGroupingByWeek.get(createWeekByDateConsiderHoliday(currentDate));
            final List<Integer> canReserveTimes = getCanReserveTimes(currentDate, currentOperationInfo, minUsingTime, reservedTimesGroupingByDate);
            canReserveTimesGroupingByDate.put(currentDate, canReserveTimes);
        }
        return canReserveTimesGroupingByDate;
    }

    private List<Integer> getCanReserveTimes(final LocalDate currentDate,
                                             final ShowOperationInfoInResponse currentOperationInfo,
                                             final int minUsingTime,
                                             final Map<LocalDate, List<ReservedTimeInfo>> reservedTimesGroupingByDate) {
        if (currentOperationInfo.isClosed()) {
            return Collections.emptyList();
        }

        final LocalTime openingTime = currentOperationInfo.isAllDay() ? LocalTime.MIN : currentOperationInfo.getStartTime();
        final LocalTime closingTime = currentOperationInfo.isAllDay() ? LocalTime.MAX : currentOperationInfo.getEndTime();

        if (!reservedTimesGroupingByDate.containsKey(currentDate)) {
            return IntStream.range(openingTime.getHour(), getHourFromClosingTime(closingTime)).boxed().toList();
        }

        final List<ReservedTimeInfo> reservedTimes = reservedTimesGroupingByDate.get(currentDate);
        final Map<Integer, Boolean> canReservePerHour = getCanReservePerHour(minUsingTime, reservedTimes, openingTime, closingTime);
        return canReservePerHour.keySet().stream().filter(canReservePerHour::get).toList();
    }

    private Map<Integer, Boolean> getCanReservePerHour(final int minUsingTime,
                                                       final List<ReservedTimeInfo> reservedTimes,
                                                       final LocalTime openingTime,
                                                       final LocalTime closingTime) {
        final Map<Integer, Boolean> canReservePerHour = initCanReservePerHour(openingTime, closingTime);
        final List<Integer> reservationStartTimes = getReservationStartTimes(closingTime, reservedTimes);
        final List<Integer> reservationEndTimes = getReservationEndTimes(openingTime, reservedTimes);

        for (int i = 0; i <= reservedTimes.size(); i++) {
            int canReserveTimeRange = reservationStartTimes.get(i) - reservationEndTimes.get(i);
            if (canReserveTimeRange >= minUsingTime) {
                IntStream.range(reservationEndTimes.get(i), reservationStartTimes.get(i)).forEach(e -> canReservePerHour.replace(e, true));
            }
        }
        return canReservePerHour;
    }

    private Map<Integer, Boolean> initCanReservePerHour(final LocalTime openingTime, final LocalTime closingTime) {
        final Map<Integer, Boolean> canReservePerHour = new ConcurrentHashMap<>();
        for (int i = openingTime.getHour(); i < getHourFromClosingTime(closingTime); i++) {
            canReservePerHour.put(i, false);
        }
        return canReservePerHour;
    }

    private List<Integer> getReservationStartTimes(final LocalTime closingTime, final List<ReservedTimeInfo> reservedTimes) {
        final List<Integer> reservationStartTimes = new ArrayList<>(reservedTimes.stream().map(r -> r.getStartTime().getHour()).toList());
        reservationStartTimes.add(reservationStartTimes.size(), getHourFromClosingTime(closingTime));
        return reservationStartTimes;
    }

    private List<Integer> getReservationEndTimes(final LocalTime openingTime, final List<ReservedTimeInfo> reservedTimes) {
        final List<Integer> reservationEndTimes = new ArrayList<>(reservedTimes.stream().map(r -> r.getEndTime().getHour()).toList());
        reservationEndTimes.add(0, openingTime.getHour());
        return reservationEndTimes;
    }

    private Week createWeekByDateConsiderHoliday(final LocalDate date) {
        final List<LocalDate> holidays = holidayProvider.getHolidays();
        return date != null ? (holidays.contains(date) ? Week.HOLIDAY : Week.of(date)) : null;
    }

    /**
     * 24시간 운영 스터디카페의 예약 가능시간대 조사시 0 ~ 23 리스트를 반환해야한다.
     * 이 때, 마감시간을 LocalTime.MAX 를 사용하는데, 이 경우 closingTime.getHour() = 23 이므로 0 ~ 22 리스트가 반환된다
     * (e.g. 09:00 ~ 21:00 이면 예약 가능시간 : 9 ~ 20 => (마감 시간) - 1 까지 만들어줘야 함. 그러나, 24시간 운영인 경우 마감 시간(getHour())이 23 이므로 공식 적용 불가)
     * 이러한 상황을 대비하기 위해 24시간 운영인 경우 마감 시간을 파싱해주는 메소드를 추가함
     * 굳이 이렇게 메소드를 사용하지 않아도 될 것 같긴하다.
     * @param closingTime 운영 마감 시간
     * @return 24시간인 경우 : 24, 그 외 : 마감 시간
     */
    private int getHourFromClosingTime(LocalTime closingTime) {
        if (closingTime.equals(LocalTime.MAX)) {
            return MAX_HOUR;
        }

        return closingTime.getHour();
    }
}
