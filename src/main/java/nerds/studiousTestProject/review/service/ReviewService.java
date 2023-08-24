package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import nerds.studiousTestProject.review.dto.find.response.TotalGradeInfo;
import nerds.studiousTestProject.review.dto.modify.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.register.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.available.response.AvailableReviewResponse;
import nerds.studiousTestProject.review.dto.delete.response.DeleteReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.FindReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.FindReviewSortedResponse;
import nerds.studiousTestProject.review.dto.modify.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.PageResponse;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.written.response.GradeInfo;
import nerds.studiousTestProject.review.dto.written.response.ReviewInfo;
import nerds.studiousTestProject.review.dto.written.response.StudycafeInfo;
import nerds.studiousTestProject.review.dto.written.response.WrittenReviewResponse;
import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.*;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SubPhotoService subPhotoService;
    private final ReservationRecordService reservationRecordService;
    private final StudycafeRepository studycafeRepository;
    private final HashtagRecordRepository hashtagRecordRepository;
    private final TokenService tokenService;
    public final Double GRADE_COUNT = 3.0;


    @Transactional
    public RegisterReviewResponse registerReview(RegisterReviewRequest registerReviewRequest){
        Grade grade = RegisterReviewRequest.toGrade(registerReviewRequest);
        grade.updateTotal(getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        Review review = Review.builder()
                .createdDate(LocalDate.now())
                .detail(registerReviewRequest.getDetail())
                .build();
        reviewRepository.save(review);
        review.addGrade(grade);

        ReservationRecord reservationRecord = reservationRecordService.findById(registerReviewRequest.getReservationId());
        reservationRecord.addReview(review);

        List<String> hashtags = registerReviewRequest.getHashtags();
        for (String userHashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder()
                    .review(review)
                    .name(HashtagName.valueOf(userHashtag))
                    .build();
            review.addHashtagRecord(hashtagRecord);
        }

        List<String> photos = registerReviewRequest.getPhotos();
        for (String photo : photos) {
            SubPhoto subPhoto = SubPhoto.builder().review(review).path(photo).build();
            subPhotoService.savePhoto(subPhoto);
        }

        return RegisterReviewResponse.builder().reviewId(review.getId()).createdAt(LocalDate.now()).build();
    }

    @Transactional
    public ModifyReviewResponse modifyReview(Long reviewId, ModifyReviewRequest modifyReviewRequest) {
        Review review = findById(reviewId);

        Grade grade = review.getGrade();
        grade.updateGrade(modifyReviewRequest.getCleanliness(),
                modifyReviewRequest.getDeafening(),
                modifyReviewRequest.getFixtureStatus(),
                modifyReviewRequest.getIsRecommend(),
                getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        review.getHashtagRecords().removeAll(review.getHashtagRecords());
        hashtagRecordRepository.deleteAllByReviewId(reviewId);
        List<String> hashtags = modifyReviewRequest.getHashtags();
        for (String userHashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder()
                    .review(review)
                    .name(HashtagName.valueOf(userHashtag))
                    .build();
            review.addHashtagRecord(hashtagRecord);
        }

        // 사진은 리뷰id를 통해 삭제하고, 다시 받아온 url로 저장을 한다.
        subPhotoService.removeAllPhotos(reviewId);
        List<String> photos = modifyReviewRequest.getPhotos();
        for (String photo : photos) {
            SubPhoto subPhoto = SubPhoto.builder().review(review).path(photo).build();
            subPhotoService.savePhoto(subPhoto);
        }

        review.updateDetail(modifyReviewRequest.getDetail());

        return ModifyReviewResponse.builder().reviewId(reviewId).modifiedAt(LocalDate.now()).build();
    }

    @Transactional
    public DeleteReviewResponse deleteReview(Long reviewId) {
        Review review = findById(reviewId);
        review.getHashtagRecords().removeAll(review.getHashtagRecords());

        hashtagRecordRepository.deleteAllByReviewId(reviewId);
        subPhotoService.removeAllPhotos(reviewId);
        reviewRepository.deleteById(reviewId);

        return DeleteReviewResponse.builder().reviewId(reviewId).deletedAt(LocalDate.now()).build();
    }

    public List<ReservationRecord> findAllReservation(Long studycafeId){
        return reservationRecordService.findAllByStudycafeId(studycafeId);
    }

    /**
     * 모든 리뷰를 보여줄 메소드(정렬까지 포함)
     * pageable를 통해서 sort까지 지정할 수 있습니다! (쿼리 파라미터를 통해 입력하면 알아서 처리해줌)
     * 그래서 기본 정렬, 평점 높은/낮은 순도 들어오는 대로 하면 되다 보니 다 합쳤습니다!
     */
    public FindReviewSortedResponse findAllReviews(Long studycafeId, Pageable pageable) {
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(pageable.getPageNumber())
                .totalPage(getAllReviews(studycafeId).size() / pageable.getPageSize()).build();
        TotalGradeInfo totalGrade = findTotalGrade(studycafeId);
        List<FindReviewResponse> reviewResponses = getReviewInfo(getAllReviewsSorted(studycafeId, pageable));
        return FindReviewSortedResponse.builder()
                .pageResponse(pageResponse)
                .totalGradeInfo(totalGrade)
                .findReviewResponses(reviewResponses)
                .build();
    }


    /**
     * 룸 별, 리뷰를 보여줄 메소드(정렬까지 포함)
     * pageable를 통해서 sort까지 지정할 수 있습니다! (쿼리 파라미터를 통해 입력하면 알아서 처리해줌)
     * 그래서 기본 정렬, 평점 높은/낮은 순도 들어오는 대로 하면 되다 보니 다 합쳤습니다!
     */
    public FindReviewSortedResponse findRoomReviews(Long studycafeId, Long roomId, Pageable pageable) {
        PageResponse pageResponse = PageResponse.builder()
                .currentPage(pageable.getPageNumber())
                .totalPage(reservationRecordService.findAllByRoomId(roomId).size() / pageable.getPageSize()).build();
        TotalGradeInfo totalGrade = findTotalGrade(studycafeId);
        List<FindReviewResponse> reviewResponses = getReviewInfo(getRoomReviewsSorted(studycafeId, roomId, pageable));
        return FindReviewSortedResponse.builder()
                .pageResponse(pageResponse)
                .totalGradeInfo(totalGrade)
                .findReviewResponses(reviewResponses)
                .build();
    }

    /**
     * 리뷰 작성 가능한 내역을 조회하는 메소드
     */
    public List<AvailableReviewResponse> findAvailableReviews(String accessToken) {
        List<ReservationRecord> reservationRecordList = getReservationRecords(accessToken);

        return  reservationRecordList.stream()
                .filter(reservationRecord -> reservationRecord.getReview() == null &&
                        !reservationRecord.getDate().plusDays(7).isBefore(LocalDate.now()))
                .map(reservationRecord -> AvailableReviewResponse.builder()
                        .reservationId(reservationRecord.getId())
                        .studycafeId(reservationRecord.getRoom().getStudycafe().getId())
                        .studycafeName(reservationRecord.getRoom().getStudycafe().getName())
                        .studycafePhoto(reservationRecord.getRoom().getStudycafe().getPhoto())
                        .roomName(reservationRecord.getRoom().getName())
                        .paymentType(reservationRecord.getPayment().getMethod())
                        .price(reservationRecord.getRoom().getPrice() * reservationRecord.getDuration())
                        .date(reservationRecord.getDate())
                        .startTime(reservationRecord.getStartTime())
                        .endTime(reservationRecord.getEndTime())
                        .duration(reservationRecord.getDuration())
                        .validDate(reservationRecord.getDate().plusDays(7))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 작성한 내역을 조회하는 메소드
     */
    public List<WrittenReviewResponse> findWrittenReviews(String accessToken, LocalDate startDate, LocalDate endDate) {
        List<ReservationRecord> reservationRecordList = getReservationRecords(accessToken);

        return reservationRecordList.stream()
                .filter(reservationRecord -> reservationRecord.getReview() != null &&
                        reservationRecord.getReview().getCreatedDate().isAfter(startDate) &&
                        reservationRecord.getReview().getCreatedDate().isBefore(endDate))
                .map(reservationRecord -> WrittenReviewResponse.builder()
                        .reservationId(reservationRecord.getId())
                        .studycafeInfo(StudycafeInfo.of(reservationRecord))
                        .gradeInfo(GradeInfo.of(reservationRecord))
                        .reviewInfo(ReviewInfo.of(reservationRecord))
                        .build())
                .collect(Collectors.toList());
    }

    public TotalGradeInfo findTotalGrade(Long studycafeId) {
        return TotalGradeInfo.builder()
                .recommendationRate(getAvgRecommendation(studycafeId))
                .cleanliness(getAvgCleanliness(studycafeId))
                .deafening(getAvgDeafening(studycafeId))
                .fixturesStatus(getAvgFixturesStatus(studycafeId))
                .total(getAvgGrade(studycafeId))
                .build();
    }

    public Integer getAvgCleanliness(Long studycafeId){
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getCleanliness();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgDeafening(Long studycafeId) {
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getDeafening();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgFixturesStatus(Long studycafeId) {
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getFixturesStatus();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgRecommendation(Long studycafeId) {
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer recommend = 0, count = 0;

        for (Review review : reviewList){
            if(review.getGrade().getIsRecommended()){
                recommend++;
            }
            count++;
        }
        return recommend / count * 100;
    }

    public Double getTotal(Integer cleanliness, Integer deafening, Integer fixtureStatus) {
        return (cleanliness + deafening + fixtureStatus) / GRADE_COUNT;
    }

    public Double getAvgGrade(Long studycafeId) {
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer count = 0;
        Double sum = 0.0;

        for (Review review : reviewList){
            sum += review.getGrade().getTotal();
            count++;
        }
        return  sum/ count;
    }

    private List<ReservationRecord> getReservationRecords(String accessToken) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        List<ReservationRecord> reservationRecordList = reservationRecordService.findAllByMemberId(member.getId());
        return reservationRecordList;
    }

    public List<FindReviewResponse> getReviewInfo(List<Review> reviewList) {
        return reviewList.stream()
                .map(review -> FindReviewResponse.builder()
                        .grade(review.getGrade().getTotal())
                        .nickname(getMember(review).getNickname())
                        .roomName(getRoom(review).getName())
                        .minHeadCount(getRoom(review).getMinHeadCount())
                        .maxHeadCount(getRoom(review).getMaxHeadCount())
                        .detail(review.getDetail())
                        .date(review.getCreatedDate())
                        .photos(subPhotoService.findReviewPhotos(review.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private Member getMember(Review review) {
        return reservationRecordService.findByReviewId(review.getId()).getMember();
    }

    private Room getRoom(Review review) {
        return reservationRecordService.findByReviewId(review.getId()).getRoom();
    }

    private List<Review> getAllReviews(Long studycafeId) {
        List<ReservationRecord> recordList = findAllReservation(studycafeId);
        List<Long> reviewIds = new ArrayList<>();
        List<Review> reviewList = new ArrayList<>();
        for (ReservationRecord reservationRecord : recordList) {
            reviewIds.add(reservationRecord.getReview().getId());
        }

        List<Review> reviews = reviewRepository.findAllByIdInOrderByCreatedDateDesc(reviewIds);

        for (int i = 0; i < reviews.size(); i++) {
            reviewList.add(reviews.get(i));
        }
        return reviewList;
    }

    private List<Review> getAllReviewsSorted(Long studycafeId, Pageable pageable) {
        pageable = getPageable(pageable);
        List<ReservationRecord> recordList = findAllReservation(studycafeId);
        List<Review> reviewList = getReviewList(pageable, recordList);
        return reviewList;
    }

    private List<Review> getRoomReviewsSorted(Long studycafeId, Long roomId, Pageable pageable) {
        pageable = getPageable(pageable);
        List<ReservationRecord> recordList = reservationRecordService.findAllByRoomId(roomId);
        List<Review> reviewList = getReviewList(pageable, recordList);
        return reviewList;
    }

    private List<Review> getReviewList(Pageable pageable, List<ReservationRecord> recordList) {
        List<Long> reviewIds = new ArrayList<>();
        List<Review> reviewList = new ArrayList<>();

        for (ReservationRecord reservationRecord : recordList) {
            reviewIds.add(reservationRecord.getReview().getId());
        }

        Page<Review> reviews = reviewRepository.findAllByIdIn(reviewIds, pageable);

        if(reviews != null && reviews.hasContent()) {
            reviewList = reviews.getContent();
        }

        return reviewList;
    }

    private PageRequest getPageable(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
    }

    private Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVEIW));
    }

    private Studycafe findByStudycafeId(Long studycafeId) {
        return studycafeRepository.findById(studycafeId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }
}