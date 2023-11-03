package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.HolidayProvider;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInfo;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.service.ReviewService;
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
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
