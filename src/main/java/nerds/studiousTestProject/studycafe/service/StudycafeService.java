package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.HolidayProvider;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.service.RoomService;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.EventCafeInfo;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.RecommendCafeInfo;
import nerds.studiousTestProject.studycafe.dto.manage.request.AnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.CafeInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.ConvenienceInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.OperationInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.RefundPolicyEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.response.AddressInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.AnnouncementResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeBasicInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.ConvenienceInfoResponse;
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
import nerds.studiousTestProject.studycafe.dto.search.request.SearchSortType;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponseInfo;
import nerds.studiousTestProject.studycafe.dto.valid.request.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.request.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.response.ValidResponse;
import nerds.studiousTestProject.studycafe.entity.Notice;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Week;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import nerds.studiousTestProject.studycafe.util.CafeRegistrationValidator;
import nerds.studiousTestProject.studycafe.util.NearestStationInfoCalculator;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class StudycafeService {
    private final MemberRepository memberRepository;
    private final CafeRegistrationValidator cafeRegistrationValidator;
    private final HashtagRecordRepository hashtagRecordRepository;
    private final HolidayProvider holidayProvider;
    private final NearestStationInfoCalculator nearestStationInfoCalculator;
    private final StorageProvider storageProvider;
    private final StudycafeRepository studycafeRepository;
    private final ReviewService reviewService;
    private final RoomService roomService;
    private final ReservationRecordRepository reservationRecordRepository;
    private final Integer TOTAL_HASHTAGS_COUNT = 5;

    /**
     * 사용자가 정한 필터 및 정렬 조건을 반영하여 알맞는 카페 정보들을 반환하는 메소드
     * 해당 메소드에서 추가적으로 잘못 입력된 값에 대한 예외처리를 진행
     * @return 검색 결과
     */
    public List<SearchResponse> inquire(String keyword, LocalDate date, LocalTime startTime, LocalTime endTime, Integer headCount, Integer minGrade, List<HashtagName> hashtags, List<ConvenienceName> conveniences, SearchSortType sortType, Pageable pageable) {
        List<LocalDate> holidays = holidayProvider.getHolidays();
        Week week = date != null ? (holidays.contains(date) ? Week.HOLIDAY : Week.of(date)) : null;

        SearchRequest searchRequest = SearchRequest.builder()
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

        List<SearchResponseInfo> searchResponseInfos = studycafeRepository.getSearchResult(searchRequest, pageable).getContent();
        return searchResponseInfos.stream().map(s -> SearchResponse.from(s, getAllHashtagNames(s), getAccumRevCnt(s), getGrade(s))).toList();
    }

    public FindStudycafeResponse findByDate(Long studycafeId, LocalDate date){
        Studycafe studycafe = findStudycafeById(studycafeId);

        return FindStudycafeResponse.builder()
                .studycafeId(studycafe.getId())
                .cafeName(studycafe.getName())
                .photos(getPhotos(studycafe))
                .accumResCnt(getAccumRevCnt(studycafeId))
                .walkingTime(studycafe.getWalkingTime())
                .nearestStation(studycafe.getNearestStation())
                .hashtags(findHashtagById(studycafe.getId()))
                .introduction(studycafe.getIntroduction())
                .conveniences(getConveniences(studycafeId))
                .announcement(getAnnouncement(studycafeId))
                .rooms(roomService.getRooms(date, studycafeId))
                .build();
    }

    public List<RefundPolicyInfo> findRefundPolicy(Long studycafeId) {
        return getRefundPolicy(studycafeId);
    }

    public List<String> findNotice(Long studycafeId) {
        return getNotice(studycafeId);
    }


    public MainPageResponse getMainPage() {
        List<RecommendCafeInfo> recommendStduycafes = getRecommendStudycafes();
        List<EventCafeInfo> eventStudycafes = getEventStudycafes();
        return MainPageResponse.builder().recommend(recommendStduycafes).event(eventStudycafes).build();
    }

    public List<RecommendCafeInfo> getRecommendStudycafes(){
        List<Studycafe> topTenCafeList = studycafeRepository.findTop10ByOrderByTotalGradeDesc();

        return topTenCafeList.stream()
                .map(studycafe -> RecommendCafeInfo.builder()
                        .studycafeId(studycafe.getId())
                        .studycafeName(studycafe.getName())
                        .photo(studycafe.getPhoto())
                        .accumRevCnt(getAccumRevCnt(studycafe.getId()))
                        .walkingTime(studycafe.getWalkingTime())
                        .nearestStation(studycafe.getNearestStation())
                        .grade(getTotalGrade(studycafe.getId()))
                        .hashtags(findHashtagById(studycafe.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<EventCafeInfo> getEventStudycafes(){
        List<Studycafe> topTenCafeList = studycafeRepository.findTop10ByOrderByCreatedDateDesc();

        return topTenCafeList.stream()
                .map(studycafe -> EventCafeInfo.builder()
                        .studycafeId(studycafe.getId())
                        .studycafeName(studycafe.getName())
                        .photo(studycafe.getPhoto())
                        .accumRevCnt(getAccumRevCnt(studycafe.getId()))
                        .walkingTime(studycafe.getWalkingTime())
                        .nearestStation(studycafe.getNearestStation())
                        .grade(getTotalGrade(studycafe.getId()))
                        .hashtags(findHashtagById(studycafe.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public Studycafe getStudyCafe(Long studycafeId) {
        return findStudycafeById(studycafeId);
    }

    public List<String> getNotice(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getNotices().stream().map(Notice::getDetail).toList();
    }

    public List<String> getConveniences(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getConveniences().stream()
                .map(Convenience::getName)
                .map(ConvenienceName::toString)
                .toList();
    }

    public List<RefundPolicyInfo> getRefundPolicy(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getRefundPolicies().stream()
                .map(RefundPolicyInfo::from)
                .collect(Collectors.toList());
    }

    public List<AnnouncementInResponse> getAnnouncement(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getAnnouncements().stream()
                .map(AnnouncementInResponse::from)
                .collect(Collectors.toList());
    }

    public List<String> findHashtagById(Long studycafeId) {
        List<HashtagName> hashtagNames = hashtagRecordRepository.findHashtagRecordByStudycafeId(studycafeId);

        int size = Math.min(hashtagNames.size(), TOTAL_HASHTAGS_COUNT);

        List<String> hashtagNameList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            hashtagNameList.add(hashtagNames.get(i).name());
        }
        return hashtagNameList;
    }

    private int getAccumRevCnt(SearchResponseInfo searchResponseInfo) {
        int total = 0;
        Integer reflectedAccumResCnt = searchResponseInfo.getReflectedAccumResCnt();
        if (reflectedAccumResCnt != null) {
            total += reflectedAccumResCnt;
        }

        Integer accumRevCnt = searchResponseInfo.getAccumRevCnt();
        if (accumRevCnt != null) {
            total += accumRevCnt;
        }

        return total;
    }

    private double getGrade(SearchResponseInfo searchResponseInfo) {
        double grade = searchResponseInfo.getReflectedTotalGrade();
        Double notReflected = searchResponseInfo.getTotalGrade();
        if (notReflected != null && notReflected != 0.) {
            grade = (grade + notReflected) / 2;
        }

        return grade;
    }

    private List<HashtagName> getAllHashtagNames(SearchResponseInfo searchResponseInfo) {
        List<HashtagName> hashtagNames = new ArrayList<>();

        if (searchResponseInfo.getAccumHashtagHistoryNames() != null) {
            hashtagNames.addAll(Arrays.stream(searchResponseInfo.getAccumHashtagHistoryNames().split(",")).map(HashtagName::valueOf).toList());
        }

        if (searchResponseInfo.getHashtagRecordNames() != null) {
            hashtagNames.addAll(Arrays.stream(searchResponseInfo.getHashtagRecordNames().split(",")).map(HashtagName::valueOf).toList());
        }
        return hashtagNames;
    }

    private Integer getAccumRevCnt(Long studycafeId) {
        return findAllReservationRecordByStudycafeId(studycafeId).size();
    }

    private List<ReservationRecord> findAllReservationRecordByStudycafeId(Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }

    private Double getTotalGrade(Long studycafeId) {
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
    public RegisterResponse register(Long memberId, RegisterRequest registerRequest) {
        // 현재 로그인된 유저 정보를 가져온다.
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));

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
                .walkingTime(nearestStationInfoResponse.getWalkingTime())
                .nearestStation(nearestStationInfoResponse.getNearestStation())
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
    public List<CafeBasicInfoResponse> inquireManagedEntryStudycafes(Long memberId, Pageable pageable) {
        Member member = findMemberFromId(memberId);

        return studycafeRepository.findByMemberOrderByCreatedDateAsc(member, pageable).getContent()
                .stream().map(CafeBasicInfoResponse::from).toList();
    }

    /**
     * 등록된 모든 스터디카페를 조회하는 메소드
     * @param memberId 사용자 pk
     * @param studycafeId 스터디카페 pk
     * @return 등록된 모든 스터디카페 정보
     */
    public CafeDetailsResponse inquireManagedStudycafe(Long memberId, Long studycafeId) {
        Member member = findMemberFromId(memberId);
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
     * @param memberId 사용자 pk
     * @param studycafeId 스터디카페 PK
     * @param cafeInfoEditRequest 수정된 데이터
     */
    @Transactional
    public void edit(Long memberId, Long studycafeId, CafeInfoEditRequest cafeInfoEditRequest) {
        Member member = findMemberFromId(memberId);
        Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));

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
     * @param memberId 사용자 PK
     * @param studycafeId 스터디카페 PK
     * @return 스터디카페의 모든 공지사항
     */
    public List<AnnouncementResponse> inquireAnnouncements(Long memberId, Long studycafeId) {
        Member member = findMemberFromId(memberId);
        Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        return studycafe.getAnnouncements().stream().map(AnnouncementResponse::from).toList();
    }

    /**
     * 공지사항 추가 로직
     * @param memberId 사용자 PK
     * @param studycafeId 스터디카페 PK
     * @param announcementRequest 공지사항 요청 값
     */
    @Transactional
    public void insertAnnouncements(Long memberId, Long studycafeId, AnnouncementRequest announcementRequest) {
        Member member = findMemberFromId(memberId);
        Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        studycafe.addAnnouncement(announcementRequest.toEntity());
    }

    /**
     * 스터디카페 삭제 메소드, 실제로 DB에서 삭제
     * @param memberId 사용자 PK
     * @param studycafeId 스터디카페 PK
     */
    @Transactional
    public void delete(Long memberId, Long studycafeId) {
        Member member = findMemberFromId(memberId);
        studycafeRepository.deleteByIdAndMember(studycafeId, member).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private Member findMemberFromId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }
}
