package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.repository.BookmarkRepository;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.HolidayProvider;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyResponse;
import nerds.studiousTestProject.reservation.dto.ReservedTimeResponse;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowOperationInfoInResponse;
import nerds.studiousTestProject.room.dto.show.ShowRoomResponse;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowStudycafeDetailsResponse;
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
    private final BookmarkRepository bookmarkRepository;

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
        return searchInResponses.stream().map(s -> SearchResponse.of(
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

    public ShowStudycafeDetailsResponse findStudycafeByDate(final Long memberId, final Long studycafeId, final LocalDate today) {
        final Studycafe studycafe = findStudycafeById(studycafeId);
        final List<Room> rooms = studycafe.getRooms();
        final Boolean bookmarked = bookmarkRepository.existsByStudycafeIdAndMemberId(studycafeId, memberId);
        final List<ShowRoomResponse> showRoomResponses = parseRoomsToEnquireRoomResponses(studycafeId, today, rooms);
        return ShowStudycafeDetailsResponse.of(studycafe, getPhotos(studycafe), showRoomResponses, bookmarked);
    }

    private List<ShowRoomResponse> parseRoomsToEnquireRoomResponses(final Long studycafeId,
                                                                    final LocalDate today,
                                                                    final List<Room> rooms) {
        return rooms.stream()
                .map(r -> ShowRoomResponse.of(r, getCanReserveDateTime(today, r.getMinUsingTime(), r.getId(), studycafeId)))
                .toList();
    }

    public List<RefundPolicyResponse> findRefundPolicies(final Long studycafeId) {
        final Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getRefundPolicies().stream()
                .map(RefundPolicyResponse::from)
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
        final Map<LocalDate, List<ReservedTimeResponse>> reservedTimesGroupingByDate = getReservedTimesGroupingByDate(roomId, today);
        final Map<Week, ShowOperationInfoInResponse> operationInfoGroupingByWeek = getOperationInfosGroupingByWeek(studycafeId);
        return getCanReserveTimesGroupingByDate(today, minUsingTime, operationInfoGroupingByWeek, reservedTimesGroupingByDate);
    }

    private Map<LocalDate, List<ReservedTimeResponse>> getReservedTimesGroupingByDate(final Long roomId, final LocalDate today) {
        final List<ReservationRecord> reservationRecords = reservationRecordRepository.findAllByFutureReservedTimeGroupingByDate(today, roomId);
        return reservationRecords.stream()
                .collect(Collectors.groupingBy(ReservationRecord::getDate, Collectors.mapping(ReservedTimeResponse::of, Collectors.toList())));
    }

    private Map<Week, ShowOperationInfoInResponse> getOperationInfosGroupingByWeek(final Long studycafeId) {
        final Map<Week, ShowOperationInfoInResponse> operationInfoGroupingByWeek = new ConcurrentHashMap<>();
        final List<OperationInfo> operationInfos = operationInfoRepository.findAllByStudycafeId(studycafeId);
        operationInfos.forEach(o -> operationInfoGroupingByWeek.put(o.getWeek(), ShowOperationInfoInResponse.from(o)));
        return operationInfoGroupingByWeek;
    }

    private Map<LocalDate, List<Integer>> getCanReserveTimesGroupingByDate(final LocalDate today,
                                                                           final int minUsingTime,
                                                                           final Map<Week, ShowOperationInfoInResponse> operationInfoGroupingByWeek,
                                                                           final Map<LocalDate, List<ReservedTimeResponse>> reservedTimesGroupingByDate) {
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
                                             final Map<LocalDate, List<ReservedTimeResponse>> reservedTimesGroupingByDate) {
        if (currentOperationInfo.isClosed()) {
            return Collections.emptyList();
        }

        final LocalTime openingTime = currentOperationInfo.isAllDay() ? LocalTime.MIN : currentOperationInfo.getStartTime();
        final LocalTime closingTime = currentOperationInfo.isAllDay() ? LocalTime.MAX : currentOperationInfo.getEndTime();

        if (!reservedTimesGroupingByDate.containsKey(currentDate)) {
            return IntStream.range(openingTime.getHour(), getHourFromClosingTime(closingTime)).boxed().toList();
        }

        final List<ReservedTimeResponse> reservedTimes = reservedTimesGroupingByDate.get(currentDate);
        final Map<Integer, Boolean> canReservePerHour = getCanReservePerHour(minUsingTime, reservedTimes, openingTime, closingTime);
        return canReservePerHour.keySet().stream().filter(canReservePerHour::get).toList();
    }

    private Map<Integer, Boolean> getCanReservePerHour(final int minUsingTime,
                                                       final List<ReservedTimeResponse> reservedTimes,
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

    private List<Integer> getReservationStartTimes(final LocalTime closingTime, final List<ReservedTimeResponse> reservedTimes) {
        final List<Integer> reservationStartTimes = new ArrayList<>(reservedTimes.stream().map(r -> r.getStartTime().getHour()).toList());
        reservationStartTimes.add(reservationStartTimes.size(), getHourFromClosingTime(closingTime));
        return reservationStartTimes;
    }

    private List<Integer> getReservationEndTimes(final LocalTime openingTime, final List<ReservedTimeResponse> reservedTimes) {
        final List<Integer> reservationEndTimes = new ArrayList<>(reservedTimes.stream().map(r -> r.getEndTime().getHour()).toList());
        reservationEndTimes.add(0, openingTime.getHour());
        return reservationEndTimes;
    }

    private Week createWeekByDateConsiderHoliday(final LocalDate date) {
        final List<LocalDate> holidays = holidayProvider.getHolidays();
        return date != null ? (holidays.contains(date) ? Week.HOLIDAY : Week.of(date)) : null;
    }

    private int getHourFromClosingTime(LocalTime closingTime) {
        if (closingTime.equals(LocalTime.MAX)) {
            return MAX_HOUR;
        }

        return closingTime.getHour();
    }
}
