package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.entity.SubPhotoType;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.dto.available.response.AvailableReviewInfo;
import nerds.studiousTestProject.review.dto.available.response.AvailableReviewResponse;
import nerds.studiousTestProject.review.dto.delete.response.DeleteReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.FindReviewInfo;
import nerds.studiousTestProject.review.dto.find.response.FindReviewSortedResponse;
import nerds.studiousTestProject.review.dto.find.response.TotalGradeInfo;
import nerds.studiousTestProject.review.dto.modify.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.modify.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.register.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.written.response.WrittenReviewInfo;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.EXPIRED_VALID_DATE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_RESERVATION_STATUS;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_WRITE_REVIEW_TIME;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_REVIEW;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final HashtagRecordRepository hashtagRecordRepository;
    private final MemberRepository memberRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    private final ReviewRepository reviewRepository;
    private final StorageProvider storageProvider;
    private final SubPhotoRepository subPhotoRepository;
    public final Double GRADE_COUNT = 3.0;

    @Transactional
    public RegisterReviewResponse register(RegisterReviewRequest registerReviewRequest, List<MultipartFile> files){
        ReservationRecord reservationRecord = findReservationRecordById(registerReviewRequest.getReservationId());
        validateReservationStatus(reservationRecord);
        validateAvailableReviewTime(reservationRecord);

        Grade grade = RegisterReviewRequest.toGrade(registerReviewRequest);
        grade.updateTotal(getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        Review review = Review.builder()
                .createdDate(LocalDate.now())
                .detail(registerReviewRequest.getDetail())
                .isRecommended(registerReviewRequest.getIsRecommend())
                .grade(grade)
                .build();
        reviewRepository.save(review);

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

        reservationRecord.addReview(review);

        return RegisterReviewResponse.builder().reviewId(review.getId()).createdAt(LocalDate.now()).build();
    }

    @Transactional
    public ModifyReviewResponse modifyReview(Long reviewId, ModifyReviewRequest modifyReviewRequest, List<MultipartFile> files) {
        Review review = findReviewById(reviewId);

        Grade grade = review.getGrade();
        grade.update(modifyReviewRequest.getCleanliness(),
                modifyReviewRequest.getDeafening(),
                modifyReviewRequest.getFixtureStatus(),
                getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        review.getHashtagRecords().removeAll(review.getHashtagRecords());
        deleteAllHashtagRecordByReviewId(reviewId);
        List<String> hashtags = modifyReviewRequest.getHashtags();
        for (String userHashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder()
                    .review(review)
                    .name(HashtagName.valueOf(userHashtag))
                    .build();
            review.addHashtagRecord(hashtagRecord);
        }

        deleteAllPhotos(reviewId);
        saveSubPhotos(review, files);

        review.updateDetail(modifyReviewRequest.getDetail());
        review.updateIsRecommended(modifyReviewRequest.getIsRecommend());

        return ModifyReviewResponse.builder().reviewId(reviewId).modifiedAt(LocalDate.now()).build();
    }

    @Transactional
    public DeleteReviewResponse deleteReview(Long reviewId) {
        ReservationRecord reservationRecord = findReservationRecordByReviewId(reviewId);
        if (LocalDate.now().isBefore(reservationRecord.getDate().plusDays(7))) {
            throw new BadRequestException(EXPIRED_VALID_DATE);
        }

        Review review = findReviewById(reviewId);
        review.getHashtagRecords().removeAll(review.getHashtagRecords());

        deleteAllHashtagRecordByReviewId(reviewId);
        deleteAllPhotos(reviewId);
        reservationRecord.deleteReview();
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
                .totalPage(reviews.getTotalPages())
                .currentPage(reviews.getNumber() + 1)
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
                .totalPage(reviews.getTotalPages())
                .currentPage(reviews.getNumber() + 1)
                .totalGradeInfo(findTotalGrade(studycafeId))
                .findReviewInfo(getReviewInfo(reviews))
                .build();
    }

    public AvailableReviewResponse findAvailableReviews(Long memberId, Pageable pageable) {
        Page<ReservationRecord> reservationRecords = getReservationRecords(memberId, pageable);

        return AvailableReviewResponse.builder()
                .totalPage(reservationRecords.getTotalPages())
                .currentPage(reservationRecords.getNumber() + 1)
                .availableReviewInfo(getAvailableReviews(reservationRecords))
                .build();
    }

    public WrittenReviewResponse findWrittenReviews(Long memberId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<ReservationRecord> reservationRecords = getReservationRecords(memberId, pageable);

        return WrittenReviewResponse.builder()
                .totalPage(reservationRecords.getTotalPages())
                .currentPage(reservationRecords.getNumber() + 1)
                .writtenReviewInfos(getWrittenReviews(reservationRecords, startDate, endDate))
                .build();
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

        if (reviewList.isEmpty()) {
            return 0;
        }

        for (Review review : reviewList){
            sum += review.getGrade().getCleanliness();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgDeafening(Long studycafeId) {
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        if (reviewList.isEmpty()) {
            return 0;
        }

        for (Review review : reviewList){
            sum += review.getGrade().getDeafening();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgFixturesStatus(Long studycafeId) {
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        if (reviewList.isEmpty()) {
            return 0;
        }

        for (Review review : reviewList){
            sum += review.getGrade().getFixturesStatus();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgRecommendation(Long studycafeId) {
        List<Review> reviewList = getAllReviews(studycafeId);
        Integer recommend = 0, count = 0;

        if (reviewList.isEmpty()) {
            return 0;
        }

        for (Review review : reviewList){
            if(review.getIsRecommended()){
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

        if (reviewList.isEmpty()) {
            return 0.0;
        }

        for (Review review : reviewList){
            sum += review.getGrade().getTotal();
            count++;
        }
        return Double.isNaN(sum/ count) ? 0.0 : sum/ count;
    }

    private void saveSubPhotos(Review review, List<MultipartFile> files) {
        List<SubPhoto> photoList = new ArrayList<>();

        String representPhoto = storageProvider.uploadFile(files.get(0));
        review.addPhoto(representPhoto);

        for (int i = 2; i < files.size(); i++) {
            String uploadFile = storageProvider.uploadFile(files.get(i));
            photoList.add(SubPhoto.builder().review(review).type(SubPhotoType.REVIEW).path(uploadFile).build());
        }
        saveAllPhotos(photoList);
    }

    private void deleteAllPhotos(Long reviewId) {
        List<String> reviewPhotos = findAllReviewPhotos(reviewId);

        for (String photoUrl : reviewPhotos) {
            storageProvider.deleteFile(photoUrl);
        }
        removeAllReviewPhotos(reviewId);
    }

    /**
     * 리뷰 작성 가능한 내역을 조회하는 메소드
     */
    private List<AvailableReviewInfo> getAvailableReviews(Page<ReservationRecord> reservationRecords) {
        List<ReservationRecord> reservationRecordList = reservationRecords.getContent();

        return  reservationRecordList.stream()
                .filter(reservationRecord -> reservationRecord.getReview() == null &&
                        !reservationRecord.getDate().plusDays(7).isBefore(LocalDate.now()))
                .map(reservationRecord -> AvailableReviewInfo.of(reservationRecord))
                .sorted(Comparator.comparing(AvailableReviewInfo::getDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 리뷰 작성한 내역을 조회하는 메소드
     */
    private List<WrittenReviewInfo> getWrittenReviews(Page<ReservationRecord> reservationRecords, LocalDate startDate, LocalDate endDate) {
        List<ReservationRecord> reservationRecordList = reservationRecords.getContent();

        return reservationRecordList.stream()
                .filter(reservationRecord -> reservationRecord.getReview() != null &&
                        reservationRecord.getReview().getCreatedDate().isAfter(startDate) &&
                        reservationRecord.getReview().getCreatedDate().isBefore(endDate))
                .map(reservationRecord -> WrittenReviewInfo.of(reservationRecord))
                .collect(Collectors.toList());
    }

    private List<ReservationRecord> getAllReservation(Long studycafeId){
        return findAllReservationRecordByStudycafeId(studycafeId);
    }

    private Page<ReservationRecord> getReservationRecords(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));
        return findAllReservationRecordByMember(member, pageable);
    }

    private List<FindReviewInfo> getReviewInfo(Page<Review> reviewList) {
        return reviewList.stream()
                .filter(review -> review != null && reviewList.hasContent())
                .map(review -> FindReviewInfo.builder()
                        .totalGrade(review.getGrade().getTotal())
                        .nickname(getMember(review).getNickname())
                        .memberPhoto(getMember(review).getPhoto())
                        .roomName(getRoom(review).getName())
                        .minHeadCount(getRoom(review).getMinHeadCount())
                        .maxHeadCount(getRoom(review).getMaxHeadCount())
                        .detail(review.getDetail())
                        .date(review.getCreatedDate())
                        .photos(findAllReviewPhotos(review.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    private Member getMember(Review review) {
        return findReservationRecordByReviewId(review.getId()).getMember();
    }

    private Room getRoom(Review review) {
        return findReservationRecordByReviewId(review.getId()).getRoom();
    }

    private List<Review> getAllReviews(Long studycafeId) {
        List<ReservationRecord> recordList = getAllReservation(studycafeId);
        List<Long> reviewIds = recordList.stream()
                .map(reservationRecord -> reservationRecord.getReview().getId())
                .collect(Collectors.toList());
        return reviewRepository.findAllByIdInOrderByCreatedDateDesc(reviewIds);
    }

    private Page<Review> getAllReviewsSorted(Long studycafeId, Pageable pageable) {
        pageable = getPageable(pageable);
        return findAllByStudycafeId(studycafeId, pageable);
    }

    private Page<Review> getRoomReviewsSorted(Long studycafeId, Long roomId, Pageable pageable) {
        pageable = getPageable(pageable);
        return findAllByRoomId(roomId, pageable);
    }

    private PageRequest getPageable(Pageable pageable) {
        Integer page = Integer.valueOf(pageable.getPageNumber());
        if(page == null || page < 1) {
            return PageRequest.of(1, pageable.getPageSize(), pageable.getSort());
        }

        return PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
    }

    private Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW));
    }

    private Page<Review> findAllByStudycafeId(Long studycafeId, Pageable pageable) {
        return reviewRepository.findAllByStudycafeId(studycafeId, pageable);
    }

    private Page<Review> findAllByRoomId(Long roomId, Pageable pageable) {
        return reviewRepository.findAllByRoomId(roomId, pageable);
    }

    private ReservationRecord findReservationRecordByReviewId(Long reviewId) {
        return reservationRecordRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    private ReservationRecord findReservationRecordById(Long reservationRecordId) {
        return reservationRecordRepository.findById(reservationRecordId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_RESERVATION_RECORD));
    }

    private List<ReservationRecord> findAllReservationRecordByStudycafeId(Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }

    private Page<ReservationRecord> findAllReservationRecordByMember(Member member, Pageable pageable) {
        return reservationRecordRepository.findAllByMember(pageable, member);
    }

    private void deleteAllHashtagRecordByReviewId(Long reviewId) {
        hashtagRecordRepository.deleteAllByReviewId(reviewId);
    }

    private void saveAllPhotos(List<SubPhoto> photos) {
        subPhotoRepository.saveAll(photos);
    }

    private void removeAllReviewPhotos(Long reviewId) {
        subPhotoRepository.deleteAllByReviewId(reviewId);
    }

    private List<String> findAllReviewPhotos(Long reviewId){
        Review review = findReviewById(reviewId);
        List<SubPhoto> photoList = subPhotoRepository.findAllByReviewId(reviewId);
        List<String> reviewPhotos = new ArrayList<>();
        reviewPhotos.add(review.getPhoto());
        List<String> reviewSubPhoto = photoList.stream().map(SubPhoto::getPath).collect(Collectors.toList());
        reviewPhotos.addAll(reviewSubPhoto);

        return reviewPhotos;
    }

    private void validateAvailableReviewTime(ReservationRecord reservationRecord) {
        if (LocalDate.now().isBefore(reservationRecord.getDate()) || LocalTime.now().isBefore(reservationRecord.getEndTime())) {
            throw new BadRequestException(INVALID_WRITE_REVIEW_TIME);
        }
    }

    private void validateReservationStatus(ReservationRecord reservationRecord) {
        if (reservationRecord.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BadRequestException(INVALID_RESERVATION_STATUS);
        }
    }
}