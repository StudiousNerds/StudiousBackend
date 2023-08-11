package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.refundpolicy.entity.RefundDay;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInResponse;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.service.RoomService;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.EventCafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.request.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.RecommendCafeResponse;
import nerds.studiousTestProject.studycafe.dto.register.request.CafeInfo;
import nerds.studiousTestProject.studycafe.dto.register.request.OperationInfoRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RefundPolicyRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RoomInfo;
import nerds.studiousTestProject.studycafe.dto.register.response.PlaceResponse;
import nerds.studiousTestProject.studycafe.dto.register.response.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.valid.request.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.request.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.response.ValidResponse;
import nerds.studiousTestProject.studycafe.entity.Notice;
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
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_BETWEEN_MAX_HEADCOUNT_AND_MIN_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_BETWEEN_STANDARD_HEADCOUNT_AND_MAX_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_BETWEEN_STANDARD_HEADCOUNT_AND_MIN_HEADCOUNT;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class StudycafeService {
    private final StudycafeRepository studycafeRepository;
    private final ReviewService reviewService;
    private final RoomService roomService;
    private final SubPhotoService subPhotoService;
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
        Studycafe studycafe = findStudycafeById(id);

        return FindStudycafeResponse.builder()
                .cafeId(studycafe.getId())
                .cafeName(studycafe.getName())
                .photos(subPhotoService.findCafePhotos(id))
                .accumResCnt(studycafe.getAccumReserveCount())
                .duration(studycafe.getDuration())
                .nearestStation(studycafe.getNearestStation())
                .hashtags((String[]) studycafe.getHashtagRecords().toArray())
                .introduction(studycafe.getIntroduction())
                .conveniences(getConveniences(id))
                .notification(studycafe.getNotificationInfo())
                .refundPolicy(getRefundPolicy(id))
                .notice(getNotice(id))
                .rooms(roomService.getRooms(findStudycafeRequest.getDate(), id))
                .recommendationRate(reviewService.getAvgRecommendation(id))
                .cleanliness(reviewService.getAvgCleanliness(id))
                .deafening(reviewService.getAvgDeafening(id))
                .fixturesStatus(reviewService.getAvgFixturesStatus(id))
                .total(studycafe.getTotalGrade())
                .reviewInfo(reviewService.findTop3Reviews(studycafe.getId()))
                .build();
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
                    .hashtags((String[]) studycafe.getHashtagRecords().toArray())
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
                    .hashtags((String[]) studycafe.getHashtagRecords().toArray())
                    .build();
            eventStudycafeList.add(foundStudycafe);
        }
        return eventStudycafeList;
    }

    public Studycafe getStudyCafe(Long studycafeId) {
        return findStudycafeById(studycafeId);
    }

    public Studycafe getStudyCafeByName(String cafeName) {
        return studycafeRepository.findByName(cafeName).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    public String[] getNotice(Long id) {
        Studycafe studycafe = findStudycafeById(id);

        List<String> noticeList = studycafe.getNotices().stream().map(Notice::getDetail).toList();
        Integer arrSize = noticeList.size();
        String notices[] = noticeList.toArray(new String[arrSize]);

        return notices;
    }

    public String[] getConveniences(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        List<ConvenienceName> convenienceList = studycafe.getConveniences().stream().map(Convenience::getName).toList();
        Integer arrSize = convenienceList.size();
        String conveniences[] = convenienceList.toArray(new String[arrSize]);

        return conveniences;
    }


    public List<RefundPolicyInResponse> getRefundPolicy(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getRefundPolicies().stream()
                .map(RefundPolicyInResponse::from)
                .collect(Collectors.toList());
    }

    private Studycafe findStudycafeById(Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    public ValidResponse validateAccountInfo(AccountInfoRequest accountInfoRequest) {
        return cafeRegistrationValidator.getAccountInfoValidResponse(accountInfoRequest);
    }

    public ValidResponse validateBusinessInfo(BusinessInfoRequest businessInfoRequest) {
        return cafeRegistrationValidator.getBusinessInfoValidResponse(businessInfoRequest);
    }

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        // 룸 정보 추가 검증
        validateRoomInfo(registerRequest);

        // 위도, 경도 정보를 통해 역 정보를 가져온다.
        CafeInfo cafeInfo = registerRequest.getCafeInfo();
        String latitude = cafeInfo.getAddressInfo().getLatitude();
        String longitude = cafeInfo.getAddressInfo().getLongitude();
        PlaceResponse placeResponse = nearestStationInfoCalculator.getPlaceResponse(latitude, longitude);

        // 생성자에서는 필요한 부분만 초기화
        Studycafe studycafe = Studycafe.builder()
                .name(cafeInfo.getName())
                .address(cafeInfo.getAddressInfo().getBasic())
                .photo(null)
                .phoneNumber(null)
                .totalGrade(0.0)
                .createdAt(LocalDateTime.now())
                .accumReserveCount(0)
                .duration(placeResponse.getDuration())
                .nearestStation(placeResponse.getNearestStation())
                .introduction(cafeInfo.getIntroduction())
                .build();

        // 유의 사항 등록
        List<String> details = cafeInfo.getNotices();
        for (String detail : details) {
            studycafe.addNotice(
                    Notice.builder()
                            .detail(detail)
                            .studycafe(studycafe)
                            .build()
            );
        }

        // 환불 정책 등록
        List<RefundPolicyRequest> refundPolicies = cafeInfo.getRefundPolicies();
        for (RefundPolicyRequest refundPolicyRequest : refundPolicies) {
            studycafe.addRefundPolicy(
                    RefundPolicy.builder()
                            .studycafe(studycafe)
                            .refundDay(RefundDay.of(refundPolicyRequest.getDay()))
                            .rate(refundPolicyRequest.getRate())
                            .type("??")
                            .build()
            );
        }

        // 운영 시간 정보 등록
        List<OperationInfoRequest> operationInfoRequests = cafeInfo.getOperationInfos();
        for (OperationInfoRequest operationInfoRequest : operationInfoRequests) {
            studycafe.addOperationInfo(
                    OperationInfo.builder()
                            .week(operationInfoRequest.getWeek())
                            .startTime(operationInfoRequest.getStartTime())
                            .endTime(operationInfoRequest.getEndTime())
                            .allDay(operationInfoRequest.getAllDay())
                            .closed(operationInfoRequest.getClosed())
                            .build()
            );
        }

        // 룸 정보 등록
        List<RoomInfo> roomInfos = registerRequest.getRoomInfos();
        for (RoomInfo roomInfo : roomInfos) {
            Room room = Room.builder()
                    .name(roomInfo.getName())
                    .studycafe(studycafe)   // 이부분 좀 애매
                    .standardHeadCount(roomInfo.getStandardHeadCount())
                    .minHeadCount(roomInfo.getMinHeadCount())
                    .maxHeadCount(roomInfo.getMaxHeadCount())
                    .minUsingTime(roomInfo.getMinUsingTime())
                    .price(roomInfo.getPrice())
                    .type(roomInfo.getType())
                    .build();

            // 룸 편의시설 정보 등록
            List<ConvenienceName> roomConveniences = roomInfo.getConveniences();
            for (ConvenienceName name : roomConveniences) {
                room.addConvenience(
                        Convenience.builder()
                                .name(name)
                                .price(0)
                                .build()
                );
            }
        }

        // 카페 편의시설 정보 등록
        List<ConvenienceName> cafeConveniences = cafeInfo.getConveniences();
        for (ConvenienceName name : cafeConveniences) {
            studycafe.addConvenience(
                    Convenience.builder()
                            .name(name)
                            .studycafe(studycafe)
                            .price(0)
                            .build()
            );
        }

        studycafeRepository.save(studycafe);    // 스터디카페 저장

        return RegisterResponse.builder()
                .cafeName(cafeInfo.getName())
                .build();
    }

    private void validateRoomInfo(RegisterRequest registerRequest) {
        List<RoomInfo> roomInfos = registerRequest.getRoomInfos();
        for (RoomInfo roomInfo : roomInfos) {
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
    }
}
