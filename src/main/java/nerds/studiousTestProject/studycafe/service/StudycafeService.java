package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.hashtag.service.HashtagRecordService;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;

import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInResponse;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.service.RoomService;

import nerds.studiousTestProject.studycafe.dto.enquiry.response.EventCafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.request.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.RecommendCafeResponse;
import nerds.studiousTestProject.studycafe.dto.manage.request.CafeInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.ConvenienceInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.AnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.OperationInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.RefundPolicyEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.response.AddressInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeBasicInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.ConvenienceInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.AnnouncementResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.OperationInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.RefundPolicyResponse;
import nerds.studiousTestProject.studycafe.dto.register.request.CafeInfoRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.ConvenienceInfoRequest;

import nerds.studiousTestProject.studycafe.dto.register.request.OperationInfoRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RefundPolicyRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RoomInfoRequest;
import nerds.studiousTestProject.studycafe.dto.register.response.AnnouncementInResponse;
import nerds.studiousTestProject.studycafe.dto.register.response.NearestStationInfoResponse;
import nerds.studiousTestProject.studycafe.dto.register.response.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.request.SortType;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.valid.request.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.request.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.response.ValidResponse;
import nerds.studiousTestProject.studycafe.entity.Notice;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import nerds.studiousTestProject.studycafe.util.CafeRegistrationValidator;
import nerds.studiousTestProject.studycafe.util.NearestStationInfoCalculator;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_DATE;
import static nerds.studiousTestProject.common.exception.ErrorCode.START_DATE_AFTER_THAN_END_DATE;
import static nerds.studiousTestProject.common.exception.ErrorCode.START_TIME_AFTER_THAN_END_TIME;
import static nerds.studiousTestProject.common.exception.ErrorCode.INVALID_BETWEEN_MAX_HEADCOUNT_AND_MIN_HEADCOUNT;
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
    private final CafeRegistrationValidator cafeRegistrationValidator;
    private final NearestStationInfoCalculator nearestStationInfoCalculator;
    private final TokenService tokenService;
    private final HashtagRecordService hashtagRecordService;
    private final ReservationRecordService reservationRecordService;

    /**
     * 사용자가 정한 필터 및 정렬 조건을 반영하여 알맞는 카페 정보들을 반환하는 메소드
     * 해당 메소드에서 추가적으로 잘못 입력된 값에 대한 예외처리를 진행
     * @param searchRequest 사용자 검색 요청값
     * @param pageable 페이지
     * @return 검색 결과
     */
    public List<SearchResponse> inquire(SearchRequest searchRequest, Pageable pageable) {

        // 이 부분도 추가 Validator 도입 예정
        if (searchRequest.getSortType() == null) {
            searchRequest.setSortType(SortType.GRADE_DESC);
        }

        // 날짜 선택이 안되었는데 시간을 선택한 경우
        if (searchRequest.getDate() == null && (searchRequest.getStartTime() != null || searchRequest.getEndTime() != null)) {
            throw new BadRequestException(NOT_FOUND_DATE);
        }

        // 시작 시간이 끝 시간보다 이후인 경우
        if (searchRequest.getStartTime() != null && searchRequest.getEndTime() != null &&
                !searchRequest.getStartTime().isBefore(searchRequest.getEndTime())) {
            throw new BadRequestException(START_TIME_AFTER_THAN_END_TIME);
        }

        return studycafeRepository.getSearchResult(searchRequest, pageable).getContent().stream().map(SearchResponse::from).toList();
    }

    public FindStudycafeResponse findByDate(Long studycafeId, FindStudycafeRequest findStudycafeRequest){
        Studycafe studycafe = findStudycafeById(studycafeId);

        return FindStudycafeResponse.builder()
                .cafeId(studycafe.getId())
                .cafeName(studycafe.getName())
                .photos(subPhotoService.findCafePhotos(studycafeId))
                .accumResCnt(getAccumResCnt(studycafeId))
                .duration(getWalkingtime(studycafe))
                .nearestStation(getNearestStation(studycafe))
                .hashtags(getHashtagRecords(studycafe.getId()))
                .introduction(studycafe.getIntroduction())
                .conveniences(getConveniences(studycafeId))
                .announcement(getAnnouncement(studycafeId))
                .rooms(roomService.getRooms(findStudycafeRequest.getDate(), studycafeId))
                .build();
    }

    public List<RefundPolicyInResponse> findRefundPolicy(Long studycafeId) {
        return getRefundPolicy(studycafeId);
    }

    public List<String> findNotice(Long studycafeId) {
        return getNotice(studycafeId);
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
            RecommendCafeResponse foundStudycafe = RecommendCafeResponse.builder()
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(studycafe.getPhoto())
                    .accumRevCnt(getAccumResCnt(studycafe.getId()))
                    .distance(getWalkingtime(studycafe))
                    .nearestStation(getNearestStation(studycafe))
                    .grade(getTotalGrade(studycafe.getId()))
                    .hashtags(getHashtagRecords(studycafe.getId()))
                    .build();
            recommedStudycafeList.add(foundStudycafe);
        }
        return recommedStudycafeList;
    }

    public List<EventCafeResponse> getEventStudycafes(){
        List<Studycafe> topTenCafeList = studycafeRepository.findTop10ByOrderByCreatedDateDesc();
        List<EventCafeResponse> eventStudycafeList = new ArrayList<>();

        for (Studycafe studycafe : topTenCafeList) {
            EventCafeResponse foundStudycafe = EventCafeResponse.builder()
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(studycafe.getPhoto())
                    .accumRevCnt(getAccumResCnt(studycafe.getId()))
                    .distance(getWalkingtime(studycafe))
                    .nearestStation(getNearestStation(studycafe))
                    .grade(getTotalGrade(studycafe.getId()))
                    .hashtags(getHashtagRecords(studycafe.getId()))
                    .build();
            eventStudycafeList.add(foundStudycafe);
        }
        return eventStudycafeList;
    }

    public Studycafe getStudyCafe(Long studycafeId) {
        return findStudycafeById(studycafeId);
    }

    public List<String> getNotice(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getNotices().stream().map(Notice::getDetail).toList();
    }

    public String[] getConveniences(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        List<ConvenienceName> convenienceList = studycafe.getConveniences().stream().map(Convenience::getName).toList();
        Integer arrSize = convenienceList.size();
        String conveniences[] = convenienceList.toArray(new String[arrSize]);

        return conveniences;
    }

    public Integer getWalkingtime(Studycafe studycafe) {
        try {
            return studycafe.getNearestStationInfo().getWalkingTime();
        } catch (Exception exception) {
            return 0;
        }
    }

    public String getNearestStation(Studycafe studycafe) {
        try {
            return studycafe.getNearestStationInfo().getNearestStation();
        } catch (Exception exception) {
            return " ";
        }
    }

    public List<RefundPolicyInResponse> getRefundPolicy(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getRefundPolicies().stream()
                .map(RefundPolicyInResponse::from)
                .collect(Collectors.toList());
    }

    public List<AnnouncementInResponse> getAnnouncement(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getAnnouncements().stream()
                .map(AnnouncementInResponse::from)
                .collect(Collectors.toList());
    }

    public String[] getHashtagRecords(Long studycafeId) {
        return hashtagRecordService.findStudycafeHashtag(studycafeId).stream().map(Enum::name).toArray(String[]::new);
    }

    public Integer getAccumResCnt(Long studycafeId) {
        return reservationRecordService.findAllByStudycafeId(studycafeId).size();
    }

    public Double getTotalGrade(Long studycafeId) {
        return reviewService.getAvgGrade(studycafeId);
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
    @Secured(value = MemberRole.ROLES.ADMIN)
    public RegisterResponse register(String accessToken, RegisterRequest registerRequest) {
        // 현재 로그인된 유저 정보를 가져온다.
        Member member = tokenService.getMemberFromAccessToken(accessToken);

        // 룸 정보 추가 검증
        validateRoomInfo(registerRequest);

        // 위도, 경도 정보를 통해 역 정보를 가져온다.
        CafeInfoRequest cafeInfo = registerRequest.getCafeInfo();
        String latitude = cafeInfo.getAddressInfo().getLatitude();
        String longitude = cafeInfo.getAddressInfo().getLongitude();
        NearestStationInfoResponse nearestStationInfoResponse = nearestStationInfoCalculator.getPlaceResponse(latitude, longitude);

        List<String> cafePhotos = cafeInfo.getPhotos();
        String cafeMainPhoto = cafePhotos.remove(0);

        // 생성자에서는 필요한 부분만 초기화
        Studycafe studycafe = Studycafe.builder()
                .name(cafeInfo.getName())
                .member(member)
                .address(cafeInfo.getAddressInfo().of())
                .photo(cafeMainPhoto)
                .tel(cafeInfo.getTel())
                .totalGrade(0.0)
                .createdDate(LocalDateTime.now())
                .accumReserveCount(0)
                .nearestStationInfo(nearestStationInfoResponse.toEmbedded())
                .introduction(cafeInfo.getIntroduction())
                .build();

        // 카페 사진 등록
        for (String cafePhotoUrl : cafePhotos) {
            studycafe.addSubPhoto(SubPhoto.builder()
                    .path(cafePhotoUrl)
                    .build()
            );
        }

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
            studycafe.addRefundPolicy(refundPolicyRequest.toEntity());
        }

        // 운영 시간 정보 등록
        List<OperationInfoRequest> operationInfoRequests = cafeInfo.getOperationInfos();
        for (OperationInfoRequest operationInfoRequest : operationInfoRequests) {
            studycafe.addOperationInfo(operationInfoRequest.toEntity());
        }

        // 룸 정보 등록
        List<RoomInfoRequest> roomInfoRequests = registerRequest.getRoomInfos();
        for (RoomInfoRequest roomInfoRequest : roomInfoRequests) {
            List<String> roomPhotos = roomInfoRequest.getPhotos();
            String roomMainPhoto = roomPhotos.remove(0);
            Room room = roomInfoRequest.toEntity(roomMainPhoto);

            // 룸 사진 등록
            for (String roomPhotoUrl : roomPhotos) {
                room.addSubPhoto(SubPhoto.builder().path(roomPhotoUrl).build());
            }

            // 룸 편의시설 정보 등록
            List<ConvenienceInfoRequest> roomConveniences = roomInfoRequest.getConvenienceInfos();
            for (ConvenienceInfoRequest convenienceInfoRequest : roomConveniences) {
                room.addConvenience(convenienceInfoRequest.toEntity());
            }

            studycafe.addRoom(room);
        }

        // 카페 편의시설 정보 등록
        List<ConvenienceInfoRequest> cafeConveniences = cafeInfo.getConvenienceInfos();
        for (ConvenienceInfoRequest convenienceInfoRequest : cafeConveniences) {
            studycafe.addConvenience(convenienceInfoRequest.toEntity());
        }

        studycafeRepository.save(studycafe);    // 스터디카페 저장

        return RegisterResponse.builder()
                .cafeName(cafeInfo.getName())
                .build();
    }

    @Secured(value = MemberRole.ROLES.ADMIN)
    public List<CafeBasicInfoResponse> inquireManagedEntryStudycafes(String accessToken, Pageable pageable) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        return studycafeRepository.findByMemberOrderByCreatedDateAsc(member, pageable).getContent()
                .stream().map(CafeBasicInfoResponse::from).toList();
    }

    /**
     * 스터디카페 등록 시 룸 정보에 대한 검증을 하는 메소드 (검증 실패 시 예외 발생)
     * @param registerRequest 스터디카페 등록 시 요청값
     */
    private void validateRoomInfo(RegisterRequest registerRequest) {
        List<RoomInfoRequest> roomInfoRequests = registerRequest.getRoomInfos();
        for (RoomInfoRequest roomInfoRequest : roomInfoRequests) {
            Integer minHeadCount = roomInfoRequest.getMinHeadCount();
            Integer maxHeadCount = roomInfoRequest.getMaxHeadCount();

            // 밑의 코드는 추후 Validator 를 도입 예정

            // 최대 인원 수가 최대 인원 수 보다 작은 경우
            if (maxHeadCount < minHeadCount) {
                throw new BadRequestException(INVALID_BETWEEN_MAX_HEADCOUNT_AND_MIN_HEADCOUNT);
            }
        }
    }

    /**
     * 등록된 모든 스터디카페를 조회하는 메소드
     * @param accessToken 사용자 엑세스 토크
     * @param studycafeId 스터디카페 pk
     * @return 등록된 모든 스터디카페 정보
     */
    @Secured(value = MemberRole.ROLES.ADMIN)
    public CafeDetailsResponse inquireManagedStudycafe(String accessToken, Long studycafeId) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        AddressInfoResponse addressInfoResponse = AddressInfoResponse.from(studycafe.getAddress());
        List<OperationInfoResponse> operationInfoResponses = studycafe.getOperationInfos().stream().map(OperationInfoResponse::from).toList();
        List<ConvenienceInfoResponse> convenienceInfoResponses = studycafe.getConveniences().stream().map(ConvenienceInfoResponse::from).toList();
        List<String> noticeResponses = studycafe.getNotices().stream().map(Notice::getDetail).toList();
        List<String> photos = getPhotos(studycafe); // 사진 : 메인 사진(단일) + 서브 사진(리스트)
        List<RefundPolicyResponse> refundPolicyResponses = studycafe.getRefundPolicies().stream().map(RefundPolicyResponse::from).toList();

        return CafeDetailsResponse.builder()
                .name(studycafe.getName())
                .addressInfo(addressInfoResponse)
                .introduction(studycafe.getIntroduction())
                .operationInfos(operationInfoResponses)
                .convenienceInfos(convenienceInfoResponses)
                .photos(photos)
                .notices(noticeResponses)
                .refundPolicies(refundPolicyResponses)
                .build();
    }

    private List<String> getPhotos(Studycafe studycafe) {
        List<String> photos = new ArrayList<>();
        photos.add(studycafe.getPhoto());
        List<String> subPhotos = studycafe.getSubPhotos().stream().map(SubPhoto::getPath).toList();
        photos.addAll(subPhotos);
        return photos;
    }

    /**
     * 등록된 스터디카페 수정 메소드
     * @param accessToken 사용자 엑세스 토큰
     * @param studycafeId 스터디카페 PK
     * @param cafeInfoEditRequest 수정된 데이터
     */
    @Secured(value = MemberRole.ROLES.ADMIN)
    @Transactional
    public void edit(String accessToken, Long studycafeId, CafeInfoEditRequest cafeInfoEditRequest) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        String introduction = cafeInfoEditRequest.getIntroduction();
        if (introduction != null) {
            studycafe.updateIntroduction(introduction);
        }

        List<OperationInfoEditRequest> operationInfoEditRequests = cafeInfoEditRequest.getOperationInfos();
        if (operationInfoEditRequests != null) {
            List<OperationInfo> operationInfos = operationInfoEditRequests.stream().map(OperationInfoEditRequest::toEntity).toList();
            studycafe.updateOperationInfos(operationInfos);
        }

        List<ConvenienceInfoEditRequest> convenienceInfoEditRequests = cafeInfoEditRequest.getConvenienceInfos();
        if (convenienceInfoEditRequests != null) {
            List<Convenience> conveniences = convenienceInfoEditRequests.stream().map(ConvenienceInfoEditRequest::toEntity).toList();
            studycafe.updateConveniences(conveniences);
        }

        // 사진은 추후

        List<String> notices = cafeInfoEditRequest.getNotices();
        if (notices != null) {
            studycafe.updateNotices(notices.stream().map(
                    n -> Notice.builder()
                            .detail(n)
                            .build()
                    ).toList()
            );
        }

        List<RefundPolicyEditRequest> refundPolicies = cafeInfoEditRequest.getRefundPolicies();
        if (refundPolicies != null) {
            studycafe.updateRefundPolices(refundPolicies.stream().map(RefundPolicyEditRequest::toEntity).toList());
        }

    }

    /**
     * 공지사항 조회 로직
     * @param accessToken 사용자 엑세스 토큰
     * @param studycafeId 스터디카페 PK
     * @return 스터디카페의 모든 공지사항
     */
    @Secured(value = MemberRole.ROLES.ADMIN)
    public List<AnnouncementResponse> inquireAnnouncements(String accessToken, Long studycafeId) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        return studycafe.getAnnouncements().stream().map(AnnouncementResponse::from).toList();
    }

    /**
     * 공지사항 추가 로직
     * @param accessToken 사용자 엑세스 토큰
     * @param studycafeId 스터디카페 PK
     * @param announcementRequest 공지사항 요청 값
     */
    @Secured(value = MemberRole.ROLES.ADMIN)
    @Transactional
    public void insertAnnouncements(String accessToken, Long studycafeId, AnnouncementRequest announcementRequest) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        // 공지 노출 시작 날짜가 끝 날짜보다 이후로 설정된 경우 (이도 마찬가지로 Validator 적용 예정)
        if (announcementRequest.getStartDate().isAfter(announcementRequest.getEndDate())) {
            throw new BadRequestException(START_DATE_AFTER_THAN_END_DATE);
        }

        studycafe.addAnnouncement(announcementRequest.toEntity());
    }

    /**
     * 스터디카페 삭제 메소드, 실제로 DB에서 삭제
     * @param accessToken 사용자 엑세스 토큰
     * @param studycafeId 스터디카페 PK
     */
    @Secured(value = MemberRole.ROLES.ADMIN)
    @Transactional
    public void deleteStudycafe(String accessToken, Long studycafeId) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        studycafeRepository.deleteByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }
}
