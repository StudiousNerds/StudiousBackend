package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.convenience.entity.ConvenienceList;
import nerds.studiousTestProject.convenience.service.ConvenienceService;
import nerds.studiousTestProject.hashtag.service.HashtagService;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.service.RoomService;
import nerds.studiousTestProject.studycafe.dto.EventCafeResponse;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.RecommendCafeResponse;
import nerds.studiousTestProject.studycafe.dto.register.PlaceResponse;
import nerds.studiousTestProject.studycafe.dto.register.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.search.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.valid.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.ValidResponse;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeDslRepository;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import nerds.studiousTestProject.studycafe.util.CafeRegistrationValidator;
import nerds.studiousTestProject.studycafe.util.NearestStationInfoCalculator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_BETWEEN_MAX_HEADCOUNT_AND_MIN_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_BETWEEN_STANDARD_HEADCOUNT_AND_MAX_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_BETWEEN_STANDARD_HEADCOUNT_AND_MIN_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudycafeService {
    private final StudycafeRepository studycafeRepository;
    private final ReviewService reviewService;
    private final RoomService roomService;
    private final SubPhotoService subPhotoService;
    private final HashtagService hashtagService;
    private final ConvenienceService convenienceService;
    private final StudycafeDslRepository studycafeDslRepository;
    private final CafeRegistrationValidator cafeRegistrationValidator;
    private final NearestStationInfoCalculator nearestStationInfoCalculator;

    /**
     * 사용자가 정한 필터 및 정렬 조건을 반영하여 알맞는 카페 정보들을 반환하는 메소드
     * 해당 메소드에서 추가적으로 잘못 입력된 값에 대한 예외처리를 진행
     * @param searchRequest 사용자 검색 요청값
     * @param pageable 페이지
     * @return 검색 결과
     */
    @Transactional(readOnly = true)
    public List<SearchResponse> inquire(SearchRequest searchRequest, Pageable pageable) {

        // 날짜 선택이 안되었는데 시간을 선택한 경우
        if (searchRequest.getDate() == null && (searchRequest.getStartTime() != null || searchRequest.getEndTime() != null)) {
            throw new BadRequestException(ErrorCode.NOT_FOUND_DATE);
        }

        // 시작 시간이 끝 시간보다 이후인 경우
        if (searchRequest.getStartTime() != null && searchRequest.getEndTime() != null &&
                !searchRequest.getStartTime().isBefore(searchRequest.getEndTime())) {
            throw new BadRequestException(ErrorCode.START_TIME_AFTER_THAN_END_TIME);
        }

        return studycafeDslRepository.searchAll(searchRequest, pageable).getContent();
    }

    public FindStudycafeResponse findByDate(Long id, FindStudycafeRequest findStudycafeRequest){
        Studycafe studycafe = studycafeRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        /*
        return FindStudycafeResponse.builder()
                .cafeId(studycafe.getId())
                .cafeName(studycafe.getName())
                .photos(subPhotoService.findCafePhotos(id))
                .accumResCnt(studycafe.getAccumReserveCount())
                .duration(studycafe.getDuration())
                .nearestStation(studycafe.getNearestStation())
                .hashtags(hashtagService.findHashtags(id))
                .introduction(studycafe.getIntroduction())
                .conveniences(convenienceService.getAllCafeConveniences(id))
                .notification(studycafe.getNotificationInfo())
                .refundPolicy(studycafe.getRefundPolicyInfo())
                .notice(getNotice(id))
                .rooms(roomService.getRooms(findStudycafeRequest.getDate(), id))
                .recommendationRate(reviewService.getAvgRecommendation(id))
                .cleanliness(reviewService.getAvgCleanliness(id))
                .deafening(reviewService.getAvgDeafening(id))
                .fixturesStatus(reviewService.getAvgFixturesStatus(id))
                .total(studycafe.getTotalGrade())
                .reviewInfo(reviewService.findAllReviews(studycafe.getId()))
                .build();

         */
        return null;
    }

    public MainPageResponse getMainPage() {
        List<RecommendCafeResponse> recommendStduycafes = getRecommendStudycafes();
        List<EventCafeResponse> eventStudycafes = getEventStudycafes();
        return MainPageResponse.builder().recommend(recommendStduycafes).event(eventStudycafes).build();
    }

    public List<RecommendCafeResponse> getRecommendStudycafes(){
        List<Studycafe> topTenCafeList = studycafeRepository.findTop10ByOrderByTotalGradeDesc();
        List<RecommendCafeResponse> recommedStudycafeList = new ArrayList<>();

        for (Studycafe studycafe : topTenCafeList) {
            String[] cafePhotos = subPhotoService.findCafePhotos(studycafe.getId());
            RecommendCafeResponse foundStudycafe = RecommendCafeResponse.builder()
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(cafePhotos[0])
                    .accumRevCnt(studycafe.getAccumReserveCount())
                    .distance(studycafe.getDuration())
                    .nearestStation(studycafe.getNearestStation())
                    .grade(studycafe.getTotalGrade())
                    .hashtags(hashtagService.findHashtags(studycafe.getId()))
                    .build();
            recommedStudycafeList.add(foundStudycafe);
        }
        return recommedStudycafeList;
    }

    public List<EventCafeResponse> getEventStudycafes(){
        List<Studycafe> topTenCafeList = studycafeRepository.findTop10ByOrderByCreatedAtDesc();
        List<EventCafeResponse> eventStudycafeList = new ArrayList<>();

        for (Studycafe studycafe : topTenCafeList) {
            String[] cafePhotos = subPhotoService.findCafePhotos(studycafe.getId());
            EventCafeResponse foundStudycafe = EventCafeResponse.builder()
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(cafePhotos[0])
                    .accumRevCnt(studycafe.getAccumReserveCount())
                    .distance(studycafe.getDuration())
                    .nearestStation(studycafe.getNearestStation())
                    .grade(studycafe.getTotalGrade())
                    .hashtags(hashtagService.findHashtags(studycafe.getId()))
                    .build();
            eventStudycafeList.add(foundStudycafe);
        }
        return eventStudycafeList;
    }

    public Studycafe getStudyCafe(Long studycafeId){
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    public Studycafe getStudyCafeByName(String cafeName){
        return studycafeRepository.findByName(cafeName).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    public String[] getNotice(Long id){
        Studycafe studycafe = studycafeRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        List<String> noticeList = studycafe.getNotice();
        Integer arrSize = noticeList.size();
        String notices[] = noticeList.toArray(new String[arrSize]);

        return notices;
    }

    public ValidResponse validateAccountInfo(AccountInfoRequest accountInfoRequest) {
        return cafeRegistrationValidator.getAccountInfoValidResponse(accountInfoRequest);
    }

    public ValidResponse validateBusinessInfo(BusinessInfoRequest businessInfoRequest) {
        return cafeRegistrationValidator.getBusinessInfoValidResponse(businessInfoRequest);
    }

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        List<RegisterRequest.RoomInfo> roomInfos = registerRequest.getRoomInfos();
        for (RegisterRequest.RoomInfo roomInfo : roomInfos) {
            Integer standardHeadCount = roomInfo.getStandardHeadCount();
            Integer minHeadCount = roomInfo.getMinHeadCount();
            Integer maxHeadCount = roomInfo.getMaxHeadCount();

            // 최대 인원 수가 최대 인원 수 보다 작은 경우
            if (maxHeadCount < minHeadCount) {
                throw new BadRequestException(INVALID_BETWEEN_MAX_HEADCOUNT_AND_MIN_HEADCOUNT);
            }

            // 기준 인원 수가 최소 인원 수 보다 작은 경우
            if (standardHeadCount < minHeadCount) {
                throw new BadRequestException(INVALID_BETWEEN_STANDARD_HEADCOUNT_AND_MIN_HEADCOUNT);
            }

            // 기준 인원 수가 최대 인원 수보다 큰 경우
            if (standardHeadCount > maxHeadCount) {
                throw new BadRequestException(INVALID_BETWEEN_STANDARD_HEADCOUNT_AND_MAX_HEADCOUNT);
            }
        }

        RegisterRequest.CafeInfo cafeInfo = registerRequest.getCafeInfo();
        String latitude = cafeInfo.getAddressInfo().getLatitude();
        String longitude = cafeInfo.getAddressInfo().getLongitude();

        PlaceResponse placeResponse = nearestStationInfoCalculator.getPlaceResponse(latitude, longitude);

        studycafeRepository.save(
                Studycafe.builder()
                        .name(cafeInfo.getName())
                        .address(cafeInfo.getAddressInfo().getBasic())
                        .photo(null)
                        .phoneNumber(null)
                        .operationInfos(
                                cafeInfo.getOperationInfos().stream().map(oi -> OperationInfo.builder()
                                                .week(oi.getWeek())
                                                .startTime(oi.getStartTime())
                                                .endTime(oi.getEndTime())
                                                .allDay(oi.getAllDay())
                                                .closed(oi.getClosed())
                                                .build()
                                ).toList()
                        )
                        .rooms(
                                roomInfos.stream().map(ri -> Room.builder()
                                        .name(ri.getName())
                                        .standardHeadCount(ri.getStandardHeadCount())
                                        .minHeadCount(ri.getMinHeadCount())
                                        .maxHeadCount(ri.getMaxHeadCount())
                                        .minUsingTime(ri.getMinUsingTime())
                                        .price(ri.getPrice())
                                        .type(ri.getType())
                                        .convenienceLists(
                                                ri.getConveniences().stream().map(c -> ConvenienceList.builder()
                                                        .name(c)
                                                        .price(0)
                                                        .build()
                                                ).toList()
                                        ).build()
                                ).toList()
                        )
                        .convenienceLists(cafeInfo.getConveniences().stream().map(
                                c -> ConvenienceList.builder()
                                        .name(c)
                                        .price(0)
                                        .build()
                                ).toList()
                        )
                        .hashtagRecords(new ArrayList<>())
                        .totalGrade(0.0)
                        .createdAt(LocalDateTime.now())
                        .accumReserveCount(0)
                        .duration(placeResponse.getDuration())
                        .nearestStation(placeResponse.getNearestStation())
                        .notice(cafeInfo.getNotices())
                        .introduction(cafeInfo.getIntroduction())
                        .refundPolicyInfo(cafeInfo.getRefundPolicies().stream().map(RegisterRequest.CafeInfo.RefundPolicy::getRate).toList())
                        .build()
        );

        return RegisterResponse.builder()
                .cafeName(cafeInfo.getName())
                .build();
    }
}
