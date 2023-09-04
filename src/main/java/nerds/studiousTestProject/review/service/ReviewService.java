package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.StorageService;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.service.HashtagRecordService;
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
import nerds.studiousTestProject.review.dto.find.response.FindReviewInfo;
import nerds.studiousTestProject.review.dto.find.response.FindReviewSortedResponse;
import nerds.studiousTestProject.review.dto.modify.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.PageInfo;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.written.response.GradeInfo;
import nerds.studiousTestProject.review.dto.written.response.ReviewInfo;
import nerds.studiousTestProject.review.dto.written.response.StudycafeInfo;
import nerds.studiousTestProject.review.dto.written.response.WrittenReviewResponse;
import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_REVEIW;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SubPhotoService subPhotoService;
    private final StorageService storageService;
    private final ReservationRecordService reservationRecordService;
    private final HashtagRecordService hashtagRecordService;
    private final TokenService tokenService;
    public final Double GRADE_COUNT = 3.0;


    @Transactional
    public RegisterReviewResponse registerReview(RegisterReviewRequest registerReviewRequest, List<MultipartFile> files){
        Grade grade = RegisterReviewRequest.toGrade(registerReviewRequest);
        grade.updateTotal(getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        Review review = Review.builder()
                .createdDate(LocalDate.now())
                .detail(registerReviewRequest.getDetail())
                .grade(grade)
                .build();
        reviewRepository.save(review);

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

        if (!files.isEmpty()) {
            saveSubPhotos(review, files);
        }

        return RegisterReviewResponse.builder().reviewId(review.getId()).createdAt(LocalDate.now()).build();
    }

    @Transactional
    public ModifyReviewResponse modifyReview(Long reviewId, ModifyReviewRequest modifyReviewRequest, List<MultipartFile> files) {
        Review review = findById(reviewId);

        Grade grade = review.getGrade();
        grade.updateGrade(modifyReviewRequest.getCleanliness(),
                modifyReviewRequest.getDeafening(),
                modifyReviewRequest.getFixtureStatus(),
                modifyReviewRequest.getIsRecommend(),
                getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        review.getHashtagRecords().removeAll(review.getHashtagRecords());
        hashtagRecordService.deleteAllByReviewId(reviewId);
        List<String> hashtags = modifyReviewRequest.getHashtags();
        for (String userHashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder()
                    .review(review)
                    .name(HashtagName.valueOf(userHashtag))
                    .build();
            review.addHashtagRecord(hashtagRecord);
        }

        // 사진은 리뷰id를 통해 삭제하고, 다시 받아온 url로 저장을 한다.
        deleteAllPhotos(reviewId);
        saveSubPhotos(review, files);

        review.updateDetail(modifyReviewRequest.getDetail());

        return ModifyReviewResponse.builder().reviewId(reviewId).modifiedAt(LocalDate.now()).build();
    }

    @Transactional
    public DeleteReviewResponse deleteReview(Long reviewId) {
        Review review = findById(reviewId);
        review.getHashtagRecords().removeAll(review.getHashtagRecords());

        hashtagRecordService.deleteAllByReviewId(reviewId);
        deleteAllPhotos(reviewId);
        reviewRepository.deleteById(reviewId);

        return DeleteReviewResponse.builder().reviewId(reviewId).deletedAt(LocalDate.now()).build();
    }

    /**
     * 모든 리뷰를 보여줄 메소드(정렬까지 포함)
     * pageable를 통해서 sort까지 지정할 수 있습니다! (쿼리 파라미터를 통해 입력하면 알아서 처리해줌)
     * 그래서 기본 정렬, 평점 높은/낮은 순도 들어오는 대로 하면 되다 보니 다 합쳤습니다!
     */
    public FindReviewSortedResponse findAllReviews(Long studycafeId, Pageable pageable) {
        Page<Review> reviews = getAllReviewsSorted(studycafeId, pageable);
        return FindReviewSortedResponse.builder()
                .pageInfo(PageInfo.of(reviews))
                .totalGradeInfo(findTotalGrade(studycafeId))
                .findReviewInfo(getReviewInfo(reviews))
                .build();
    }

    /**
     * 룸 별, 리뷰를 보여줄 메소드(정렬까지 포함)
     * pageable를 통해서 sort까지 지정할 수 있습니다! (쿼리 파라미터를 통해 입력하면 알아서 처리해줌)
     * 그래서 기본 정렬, 평점 높은/낮은 순도 들어오는 대로 하면 되다 보니 다 합쳤습니다!
     */
    public FindReviewSortedResponse findRoomReviews(Long studycafeId, Long roomId, Pageable pageable) {
        Page<Review> reviews = getRoomReviewsSorted(studycafeId, roomId, pageable);
        return FindReviewSortedResponse.builder()
                .pageInfo(PageInfo.of(reviews))
                .totalGradeInfo(findTotalGrade(studycafeId))
                .findReviewInfo(getReviewInfo(reviews))
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
                .map(reservationRecord -> AvailableReviewResponse.of(reservationRecord))
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
        return Double.isNaN(sum/ count) ? 0.0 : sum/ count;
    }

    private void saveSubPhotos(Review review, List<MultipartFile> files) {
        List<SubPhoto> photoList = new ArrayList<>();

        for (MultipartFile file : files) {
            String photoUrl = storageService.uploadFile(file);
            photoList.add(SubPhoto.builder().review(review).type("REVIEW").path(photoUrl).build());
        }
        subPhotoService.saveAllPhotos(photoList);
    }

    private void deleteAllPhotos(Long reviewId) {
        List<String> reviewPhotos = subPhotoService.findReviewPhotos(reviewId);

        for (String photoUrl : reviewPhotos) {
            storageService.deleteFile(photoUrl);
        }
        subPhotoService.removeAllPhotos(reviewId);
    }

    private List<ReservationRecord> getAllReservation(Long studycafeId){
        return reservationRecordService.findAllByStudycafeId(studycafeId);
    }

    private List<ReservationRecord> getReservationRecords(String accessToken) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        List<ReservationRecord> reservationRecordList = reservationRecordService.findAllByMemberId(member.getId());
        return reservationRecordList;
    }

    public List<FindReviewInfo> getReviewInfo(Page<Review> reviewList) {
        return reviewList.stream()
                .filter(review -> review != null && reviewList.hasContent())
                .map(review -> FindReviewInfo.builder()
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
        List<ReservationRecord> recordList = getAllReservation(studycafeId);
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

    private Page<Review> getAllReviewsSorted(Long studycafeId, Pageable pageable) {
        pageable = getPageable(pageable);
        List<ReservationRecord> recordList = getAllReservation(studycafeId);
        return getReviewList(pageable, recordList);
    }

    private Page<Review> getRoomReviewsSorted(Long studycafeId, Long roomId, Pageable pageable) {
        pageable = getPageable(pageable);
        List<ReservationRecord> recordList = reservationRecordService.findAllByRoomId(roomId);
        return getReviewList(pageable, recordList);
    }

    private Page<Review> getReviewList(Pageable pageable, List<ReservationRecord> recordList) {
        List<Long> reviewIds = new ArrayList<>();

        for (ReservationRecord reservationRecord : recordList) {
            reviewIds.add(reservationRecord.getReview().getId());
        }

        return reviewRepository.findAllByIdIn(reviewIds, pageable);
    }

    private PageRequest getPageable(Pageable pageable) {
        Integer page = Integer.valueOf(pageable.getPageNumber());
        if(page == null || page < 1) {
            return PageRequest.of(1, pageable.getPageSize(), pageable.getSort());
        }

        return PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
    }

    private Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW));
    }
}