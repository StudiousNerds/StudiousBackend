package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.HolidayProvider;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
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
import nerds.studiousTestProject.studycafe.dto.modify.request.AnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.CafeInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.ConvenienceInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.OperationInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.RefundPolicyEditRequest;
import nerds.studiousTestProject.studycafe.dto.modify.response.AddressInfoResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.AnnouncementResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.CafeBasicInfoResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.CafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ConvenienceInfoResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.OperationInfoResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.RefundPolicyResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_CAFE_MAIN_PHOTO_SIZE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_ROOM_PHOTOS;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;
import static nerds.studiousTestProject.photo.entity.SubPhotoType.ROOM;
import static nerds.studiousTestProject.photo.entity.SubPhotoType.STUDYCAFE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class StudycafeService {
    private final HolidayProvider holidayProvider;
    private final OperationInfoRepository operationInfoRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    private final StudycafeRepository studycafeRepository;

    /**
     * 사용자가 정한 필터 및 정렬 조건을 반영하여 알맞는 카페 정보들을 반환하는 메소드
     * 해당 메소드에서 추가적으로 잘못 입력된 값에 대한 예외처리를 진행
     * @return 검색 결과
     */
    public List<SearchResponse> enquiry(final String keyword,
                                        final LocalDate date,
                                        final LocalTime startTime,
                                        final LocalTime endTime,
                                        final Integer headCount,
                                        final Integer minGrade,
                                        final List<HashtagName> hashtags,
                                        final List<ConvenienceName> conveniences,
                                        final SearchSortType sortType,
                                        final Pageable pageable) {
        final List<LocalDate> holidays = holidayProvider.getHolidays();
        final Week week = date != null ? (holidays.contains(date) ? Week.HOLIDAY : Week.of(date)) : null;

        final SearchRequest searchRequest = SearchRequest.builder()
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

        final List<SearchResponseInfo> searchResponseInfos = studycafeRepository.getSearchResult(searchRequest, pageable).getContent();
        return searchResponseInfos.stream().map(s -> SearchResponse.from(s, getAllHashtagNames(s), getAccumRevCnt(s), getGrade(s))).toList();
    }

    public FindStudycafeResponse findByDate(final Long studycafeId, final LocalDate date){
        final Studycafe studycafe = findStudycafeById(studycafeId);

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

    public List<RefundPolicyInfo> findRefundPolicy(final Long studycafeId) {
        return getRefundPolicy(studycafeId);
    }

    public List<String> findNotice(final Long studycafeId) {
        return getNotice(studycafeId);
    }

    public ShowDetailsResponse findStudycafeByDate(final Long studycafeId, final LocalDate today) {
        final Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getNotices().stream().map(Notice::getDetail).toList();
    }

    public List<String> getConveniences(final Long studycafeId) {
        final Studycafe studycafe = findStudycafeById(studycafeId);

        return studycafe.getConveniences().stream()
                .map(Convenience::getName)
                .map(ConvenienceName::toString)
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

        return studycafe.getAnnouncements().stream()
                .map(AnnouncementInResponse::from)
                .collect(Collectors.toList());
    }

    private List<String> findHashtagById(final Long studycafeId) {
        final List<HashtagName> hashtagNames = hashtagRecordRepository.findHashtagRecordByStudycafeId(studycafeId);

        final int size = Math.min(hashtagNames.size(), TOTAL_HASHTAGS_COUNT);

        final List<String> hashtagNameList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            hashtagNameList.add(hashtagNames.get(i).name());
        }
        return hashtagNameList;
    }

    private int getAccumRevCnt(final SearchResponseInfo searchResponseInfo) {
        int total = 0;
        final Integer reflectedAccumRevCnt = searchResponseInfo.getReflectedAccumRevCnt();
        if (reflectedAccumRevCnt != null) {
            total += reflectedAccumRevCnt;
        }

        final Integer accumRevCnt = searchResponseInfo.getAccumRevCnt();
        if (accumRevCnt != null) {
            total += accumRevCnt;
        }

        return total;
    }

    private double getGrade(final SearchResponseInfo searchResponseInfo) {
        final Double reflectedGradeSum = searchResponseInfo.getReflectedGradeSum();
        final Double gradeSum = searchResponseInfo.getGradeSum();
        final double sum = (reflectedGradeSum != null ? reflectedGradeSum : 0.) + (gradeSum != null ? gradeSum : 0.);

        final Integer reflectedGradeCount = searchResponseInfo.getReflectedGradeCount();
        final Integer gradeCount = searchResponseInfo.getGradeCount();
        final int count = (reflectedGradeCount != null ? reflectedGradeCount : 0) + (gradeCount != null ? gradeCount : 0);

        return sum / Math.max(count, 1); // count 값이 0인 경우 NaN이 발생하는 오류 핸들링
    }

    private List<HashtagName> getAllHashtagNames(final SearchResponseInfo searchResponseInfo) {
        final List<HashtagName> hashtagNames = new ArrayList<>();

        if (searchResponseInfo.getAccumHashtags() != null) {
            hashtagNames.addAll(Arrays.stream(searchResponseInfo.getAccumHashtags().split(",")).map(HashtagName::valueOf).toList());
        }

        if (searchResponseInfo.getHashtags() != null) {
            hashtagNames.addAll(Arrays.stream(searchResponseInfo.getHashtags().split(",")).map(HashtagName::valueOf).toList());
        }

        return hashtagNames;
    }

    private Integer getAccumRevCnt(final Long studycafeId) {
        return findAllReservationRecordByStudycafeId(studycafeId).size();
    }

    private List<ReservationRecord> findAllReservationRecordByStudycafeId(final Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }

    private Double getTotalGrade(final Long studycafeId) {
        return reviewService.getAvgGrade(studycafeId);
    }

    private Studycafe findStudycafeById(final Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    public ValidResponse validateAccountInfo(final AccountInfoRequest accountInfoRequest) {
        return cafeRegistrationValidator.getAccountInfoValidResponse(accountInfoRequest);
    }

    public ValidResponse validateBusinessInfo(final BusinessInfoRequest businessInfoRequest) {
        return cafeRegistrationValidator.getBusinessInfoValidResponse(businessInfoRequest);
    }

    @Transactional
    public RegisterResponse register(final Long memberId,
                                     final RegisterRequest registerRequest,
                                     final MultiValueMap<String, MultipartFile> multipartFileMap) {
        final CafeInfoRequest cafeInfo = registerRequest.getCafeInfo();
        final List<RoomInfoRequest> roomInfos = registerRequest.getRoomInfos();
        validatePhotos(multipartFileMap, roomInfos);    // 사진에 대한 검증

        final Member member = findMemberById(memberId);     // 현재 로그인된 유저 정보를 가져온다.

        // 위도, 경도 정보를 통해 역 정보를 가져온다.
        final NearestStationInfoResponse nearestStationInfoResponse = getNearestStationInfoResponse(cafeInfo);
        final Studycafe studycafe = cafeInfo.toStudycafe(member, nearestStationInfoResponse);
        addStudycafeInfos(multipartFileMap, cafeInfo, studycafe);   // 스터디카페 관련 정보들을 등록

        addRoomInfos(roomInfos, multipartFileMap, studycafe);   // 룸 정보 등록

        studycafeRepository.save(studycafe);    // 스터디카페 저장

        return RegisterResponse.builder()
                .cafeName(cafeInfo.getName())
                .build();
    }

    private void validatePhotos(final MultiValueMap<String, MultipartFile> multipartFileMap, final List<RoomInfoRequest> roomInfoRequests) {
        // 카페 대표 사진이 없는 경우
        if (!multipartFileMap.containsKey(CAFE_MAIN_PHOTO_KEY) || multipartFileMap.get(CAFE_MAIN_PHOTO_KEY).size() != 1) {
            throw new BadRequestException(INVALID_CAFE_MAIN_PHOTO_SIZE);
        }

        // 룸 사진 개수가 DTO의 룸 개수와 다른 경우
        final int size = roomInfoRequests.size();
        final long count = multipartFileMap.keySet().stream().filter(f -> f.contains(ROOM_PHOTOS_KEY)).count();
        if (size != count) {
            throw new BadRequestException(INVALID_ROOM_PHOTOS);
        }

        for (int i = 0; i < size; i++) {
            final List<MultipartFile> multipartFiles = multipartFileMap.get(ROOM_PHOTOS_KEY + i);
            if (multipartFiles == null || multipartFiles.isEmpty()) {
                throw new BadRequestException(INVALID_ROOM_PHOTOS);
            }
        }
    }

    private void addStudycafeInfos(final MultiValueMap<String, MultipartFile> multipartFileMap,
                                   final CafeInfoRequest cafeInfo,
                                   final Studycafe studycafe) {
        addCafeMainPhoto(multipartFileMap, studycafe);  // 카페 메인 사진 등록
        addCafeSubPhotos(multipartFileMap, studycafe);  // 카페 서브 사진 등록
        addNotices(cafeInfo, studycafe);                // 유의 사항 등록
        addRefundPolices(cafeInfo, studycafe);          // 환불 정책 등록
        addOperationInfos(cafeInfo, studycafe);         // 운영 시간 정보 등록
        addCafeConveniences(cafeInfo, studycafe);       // 카페 편의시설 정보 등록
    }

    private void addCafeMainPhoto(final MultiValueMap<String, MultipartFile> multipartFileMap, final Studycafe studycafe) {
        final String cafeMainPhoto = storageProvider.uploadFile(multipartFileMap.getFirst(CAFE_MAIN_PHOTO_KEY));
        studycafe.updatePhoto(cafeMainPhoto);   // 카페 메인 사진 등록
    }

    private NearestStationInfoResponse getNearestStationInfoResponse(final CafeInfoRequest cafeInfo) {
        final String latitude = cafeInfo.getAddressInfo().getLatitude();
        final String longitude = cafeInfo.getAddressInfo().getLongitude();
        return nearestStationInfoCalculator.getPlaceResponse(latitude, longitude);
    }

    private void addCafeSubPhotos(final MultiValueMap<String, MultipartFile> multipartFileMap, final Studycafe studycafe) {
        final List<MultipartFile> cafeSubPhotos = multipartFileMap.get(CAFE_SUB_PHOTOS_KEY);
        for (MultipartFile cafeSubPhoto : cafeSubPhotos) {
            final String photoUrl = storageProvider.uploadFile(cafeSubPhoto);
            studycafe.addSubPhoto(SubPhoto.builder().path(photoUrl).type(STUDYCAFE).build());
        }
    }

    private void addRoomInfos(final List<RoomInfoRequest> roomInfoRequests,
                              final MultiValueMap<String, MultipartFile> multipartFileMap,
                              final Studycafe studycafe) {
        for (int i = 0; i < roomInfoRequests.size(); i++) {
            final RoomInfoRequest roomInfoRequest = roomInfoRequests.get(i);
            final Room room = roomInfoRequest.toEntity();
            final List<ConvenienceInfoRequest> convenienceInfos = roomInfoRequest.getConvenienceInfos();

            addRoomSubPhotos(multipartFileMap, ROOM_PHOTOS_KEY + i, room);  // 룸 사진 등록
            addRoomConveniences(convenienceInfos, room); // 룸 편의시설 정보 등록
            studycafe.addRoom(room);
        }
    }

    private void addRoomConveniences(final List<ConvenienceInfoRequest> convenienceInfoRequests, final Room room) {
        for (ConvenienceInfoRequest convenienceInfoRequest : convenienceInfoRequests) {
            room.addConvenience(convenienceInfoRequest.toEntity());
        }
    }

    private void addRoomSubPhotos(final MultiValueMap<String, MultipartFile> multipartFileMap,
                                  final String key,
                                  final Room room) {
        final List<MultipartFile> roomSubPhotos = multipartFileMap.get(key);
        for (MultipartFile roomSubPhoto : roomSubPhotos) {
            final String roomSubPhotoUrl = storageProvider.uploadFile(roomSubPhoto);
            room.addSubPhoto(SubPhoto.builder().path(roomSubPhotoUrl).type(ROOM).build());
        }
    }

    private void addCafeConveniences(final CafeInfoRequest cafeInfo, final Studycafe studycafe) {
        final List<ConvenienceInfoRequest> cafeConveniences = cafeInfo.getConvenienceInfos();
        for (ConvenienceInfoRequest convenienceInfoRequest : cafeConveniences) {
            studycafe.addConvenience(convenienceInfoRequest.toEntity());
        }
    }

    private void addOperationInfos(final CafeInfoRequest cafeInfo, final Studycafe studycafe) {
        final List<OperationInfoRequest> operationInfoRequests = cafeInfo.getOperationInfos();
        for (OperationInfoRequest operationInfoRequest : operationInfoRequests) {
            studycafe.addOperationInfo(operationInfoRequest.toEntity());
        }
    }

    private void addRefundPolices(final CafeInfoRequest cafeInfo, final Studycafe studycafe) {
        final List<RefundPolicyRequest> refundPolicies = cafeInfo.getRefundPolicies();
        for (RefundPolicyRequest refundPolicyRequest : refundPolicies) {
            studycafe.addRefundPolicy(refundPolicyRequest.toEntity());
        }
    }

    private void addNotices(final CafeInfoRequest cafeInfo, final Studycafe studycafe) {
        final List<String> noticeDetails = cafeInfo.getNotices();
        for (String noticeDetail : noticeDetails) {
            studycafe.addNotice(Notice.builder()
                    .detail(noticeDetail)
                    .studycafe(studycafe)
                    .build()
            );
        }
    }

    public List<CafeBasicInfoResponse> enquiryManagedEntryStudycafes(final Long memberId, final Pageable pageable) {
        final Member member = findMemberById(memberId);
        return studycafeRepository.findByMemberOrderByCreatedDateAsc(member, pageable).getContent()
                .stream().map(CafeBasicInfoResponse::from).toList();
    }

    /**
     * 등록된 모든 스터디카페를 조회하는 메소드
     * @param memberId 사용자 pk
     * @param studycafeId 스터디카페 pk
     * @return 등록된 모든 스터디카페 정보
     */
    public CafeDetailsResponse enquiryManagedStudycafe(final Long memberId, final Long studycafeId) {
        final Member member = findMemberById(memberId);
        final Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        final AddressInfoResponse addressInfoResponse = AddressInfoResponse.from(studycafe.getAddress());
        final List<OperationInfoResponse> operationInfoResponses = studycafe.getOperationInfos().stream().map(OperationInfoResponse::from).toList();
        final List<ConvenienceInfoResponse> convenienceInfoResponses = studycafe.getConveniences().stream().map(ConvenienceInfoResponse::from).toList();
        final List<String> noticeResponses = studycafe.getNotices().stream().map(Notice::getDetail).toList();
        final List<String> photos = getPhotos(studycafe); // 사진 : 메인 사진(단일) + 서브 사진(리스트)
        final List<RefundPolicyResponse> refundPolicyResponses = studycafe.getRefundPolicies().stream().map(RefundPolicyResponse::from).toList();

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

    private List<String> getPhotos(final Studycafe studycafe) {
        final List<String> photos = new ArrayList<>();
        photos.add(studycafe.getPhoto());

        final List<String> subPhotos = studycafe.getSubPhotos().stream().map(SubPhoto::getPath).toList();
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
    public void modify(final Long memberId,
                       final Long studycafeId,
                       final CafeInfoEditRequest cafeInfoEditRequest) {
        final Member member = findMemberById(memberId);
        final Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        final String introduction = cafeInfoEditRequest.getIntroduction();
        if (introduction != null) {
            studycafe.updateIntroduction(introduction);
        }

        final List<OperationInfoEditRequest> operationInfoEditRequests = cafeInfoEditRequest.getOperationInfos();
        if (operationInfoEditRequests != null) {
            final List<OperationInfo> operationInfos = operationInfoEditRequests.stream()
                    .map(OperationInfoEditRequest::toEntity)
                    .toList();
            studycafe.updateOperationInfos(operationInfos);
        }

        final List<ConvenienceInfoEditRequest> convenienceInfoEditRequests = cafeInfoEditRequest.getConvenienceInfos();
        if (convenienceInfoEditRequests != null) {
            final List<Convenience> conveniences = convenienceInfoEditRequests.stream()
                    .map(ConvenienceInfoEditRequest::toEntity)
                    .toList();
            studycafe.updateConveniences(conveniences);
        }

        // 사진은 추후

        final List<String> noticeRequests = cafeInfoEditRequest.getNotices();
        if (noticeRequests != null) {
            List<Notice> notices = noticeRequests.stream()
                    .map(n -> Notice.builder()
                            .detail(n)
                            .build()
                    ).toList();
            studycafe.updateNotices(notices);
        }

        final List<RefundPolicyEditRequest> refundPolicyRequests = cafeInfoEditRequest.getRefundPolicies();
        if (refundPolicyRequests != null) {
            List<RefundPolicy> refundPolicies = refundPolicyRequests.stream()
                    .map(RefundPolicyEditRequest::toEntity)
                    .toList();
            studycafe.updateRefundPolices(refundPolicies);
        }

    }

    /**
     * 공지사항 조회 로직
     * @param memberId 사용자 PK
     * @param studycafeId 스터디카페 PK
     * @return 스터디카페의 모든 공지사항
     */
    public List<AnnouncementResponse> enquiryAnnouncements(final Long memberId, final Long studycafeId) {
        final Member member = findMemberById(memberId);
        final Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        return studycafe.getAnnouncements().stream().map(AnnouncementResponse::from).toList();
    }

    /**
     * 공지사항 추가 로직
     * @param memberId 사용자 PK
     * @param studycafeId 스터디카페 PK
     * @param announcementRequest 공지사항 요청 값
     */
    @Transactional
    public void registerAnnouncements(final Long memberId,
                                      final Long studycafeId,
                                      final AnnouncementRequest announcementRequest) {
        final Member member = findMemberById(memberId);
        final Studycafe studycafe = studycafeRepository.findByIdAndMember(studycafeId, member).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));

        studycafe.addAnnouncement(announcementRequest.toEntity());
    }

    /**
     * 스터디카페 삭제 메소드, 실제로 DB에서 삭제
     * @param memberId 사용자 PK
     * @param studycafeId 스터디카페 PK
     */
    @Transactional
    public void delete(final Long memberId, final Long studycafeId) {
        final Member member = findMemberById(memberId);
        studycafeRepository.deleteByIdAndMember(studycafeId, member).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }
}
