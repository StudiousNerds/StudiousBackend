package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.hashtag.entity.AccumHashtagHistory;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.repository.AccumHashtagHistoryRepository;
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
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Page;
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
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_HASHTAG;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_RESERVATION_RECORD;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_REVIEW;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;


@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final AccumHashtagHistoryRepository accumHashtagHistoryRepository;
    private final HashtagRecordRepository hashtagRecordRepository;
    private final MemberRepository memberRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    private final ReviewRepository reviewRepository;
    private final StorageProvider storageProvider;
    private final SubPhotoRepository subPhotoRepository;
    private final StudycafeRepository studycafeRepository;
    public final Double GRADE_COUNT = 3.0;

    @Transactional
    public RegisterReviewResponse register(RegisterReviewRequest registerRequest, List<MultipartFile> files){
        ReservationRecord reservation = findReservationRecordById(registerRequest.getReservationId());
        validateReservationStatus(reservation);
        validateAvailableReviewTime(reservation);
        Studycafe studycafe = reservation.getRoom().getStudycafe();

        Grade grade = RegisterReviewRequest.toGrade(registerRequest);
        grade.updateTotal(getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));
        studycafe.registerGrade(getGradeSum(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        Review review = Review.builder()
                .createdDate(LocalDate.now())
                .detail(registerRequest.getDetail())
                .isRecommended(registerRequest.getIsRecommend())
                .grade(grade)
                .build();
        reviewRepository.save(review);
        updateHashtagRecord(registerRequest.getHashtags(), review, studycafe);

        if (!files.isEmpty()) {
            saveSubPhotos(review, files);
        }

        reservation.addReview(review);

        return RegisterReviewResponse.builder().reviewId(review.getId()).createdAt(LocalDate.now()).build();
    }

    @Transactional
    public ModifyReviewResponse modifyReview(Long reviewId, ModifyReviewRequest modifyRequest, List<MultipartFile> files) {
        Review review = findReviewById(reviewId);
        Studycafe studycafe = findStudycafeById(modifyRequest.getCafeId());
        Double modifiedTotal = getTotal(modifyRequest.getCleanliness(), modifyRequest.getDeafening(), modifyRequest.getFixtureStatus());

        Grade grade = review.getGrade();
        grade.update(modifyRequest.getCleanliness(),
                modifyRequest.getDeafening(),
                modifyRequest.getFixtureStatus(),
                modifiedTotal);
        studycafe.updateGrade(modifiedTotal);

        deleteHashtagRecords(reviewId, studycafe);
        review.getHashtagRecords().removeAll(review.getHashtagRecords());
        updateHashtagRecord(modifyRequest.getHashtags(), review, studycafe);
        deleteAllPhotos(reviewId);

        if (!files.isEmpty()) {
            saveSubPhotos(review, files);
        }

        review.updateDetail(modifyRequest.getDetail());
        review.updateIsRecommended(modifyRequest.getIsRecommend());

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
        Studycafe studycafe = reservationRecord.getRoom().getStudycafe();
        deleteHashtagRecords(reviewId, studycafe);
        deleteGradeRecord(studycafe, review.getGrade().getTotal());
        deleteAllPhotos(reviewId);
        reservationRecord.deleteReview();
        reviewRepository.deleteById(reviewId);

        return DeleteReviewResponse.builder().reviewId(reviewId).deletedAt(LocalDate.now()).build();
    }

    public FindReviewSortedResponse findAllReviews(Long studycafeId, Pageable pageable) {
        Page<Review> reviews = getAllReviewsSorted(studycafeId, pageable);
        return FindReviewSortedResponse.builder()
                .totalPage(reviews.getTotalPages())
                .currentPage(reviews.getNumber() + 1)
                .totalGradeInfo(findTotalGrade(studycafeId))
                .findReviewInfo(getReviewInfo(reviews))
                .build();
    }

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
        Page<ReservationRecord> reservations = getReservationRecords(memberId, pageable);
        return AvailableReviewResponse.builder()
                .totalPage(reservations.getTotalPages())
                .currentPage(reservations.getNumber() + 1)
                .availableReviewInfo(getAvailableReviews(reservations))
                .build();
    }

    public WrittenReviewResponse findWrittenReviews(Long memberId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<ReservationRecord> reservations = getReservationRecords(memberId, pageable);
        return WrittenReviewResponse.builder()
                .totalPage(reservations.getTotalPages())
                .currentPage(reservations.getNumber() + 1)
                .writtenReviewInfos(getWrittenReviews(reservations, startDate, endDate))
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
        List<Review> reviews = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        if (reviews.isEmpty()) {
            return 0;
        }

        for (Review review : reviews){
            sum += review.getGrade().getCleanliness();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgDeafening(Long studycafeId) {
        List<Review> reviews = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        if (reviews.isEmpty()) {
            return 0;
        }

        for (Review review : reviews){
            sum += review.getGrade().getDeafening();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgFixturesStatus(Long studycafeId) {
        List<Review> reviews = getAllReviews(studycafeId);
        Integer count = 0, sum = 0;

        if (reviews.isEmpty()) {
            return 0;
        }

        for (Review review : reviews){
            sum += review.getGrade().getFixturesStatus();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgRecommendation(Long studycafeId) {
        List<Review> reviews = getAllReviews(studycafeId);
        Integer recommend = 0, count = 0;

        if (reviews.isEmpty()) {
            return 0;
        }

        for (Review review : reviews){
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

    public Double getGradeSum(Integer cleanliness, Integer deafening, Integer fixtureStatus) {
        return Double.valueOf(cleanliness + deafening + fixtureStatus);
    }

    public Double getAvgGrade(Long studycafeId) {
        Studycafe studycafe = findStudycafeById(studycafeId);
        Double sum = studycafe.getGradeSum();
        Integer count = studycafe.getGradeCount();
        return Double.isNaN(sum/ count) ? 0.0 : sum/ count;
    }

    private void saveSubPhotos(Review review, List<MultipartFile> files) {
        List<SubPhoto> photos = new ArrayList<>();
        String representPhoto = storageProvider.uploadFile(files.get(0));
        review.addPhoto(representPhoto);

        for (int i = 2; i < files.size(); i++) {
            String uploadFile = storageProvider.uploadFile(files.get(i));
            photos.add(SubPhoto.builder().review(review).type(SubPhotoType.REVIEW).path(uploadFile).build());
        }

        saveAllPhotos(photos);
    }

    private void deleteAllPhotos(Long reviewId) {
        List<String> reviewPhotos = findAllReviewPhotos(reviewId);
        for (String photoUrl : reviewPhotos) {
            storageProvider.deleteFile(photoUrl);
        }
        removeAllReviewPhotos(reviewId);
    }

    private List<AvailableReviewInfo> getAvailableReviews(Page<ReservationRecord> reservations) {
        List<ReservationRecord> reservationList = reservations.getContent();

        return  reservationList.stream()
                .filter(reservation -> reservation.getReview() == null &&
                        !reservation.getDate().plusDays(7).isBefore(LocalDate.now()))
                .map(reservation -> AvailableReviewInfo.from(reservation))
                .sorted(Comparator.comparing(AvailableReviewInfo::getDate).reversed())
                .collect(Collectors.toList());
    }

    private List<WrittenReviewInfo> getWrittenReviews(Page<ReservationRecord> reservations, LocalDate startDate, LocalDate endDate) {
        List<ReservationRecord> reservationList = reservations.getContent();

        return reservationList.stream()
                .filter(reservation -> reservation.getReview() != null &&
                        reservation.getReview().getCreatedDate().isAfter(startDate) &&
                        reservation.getReview().getCreatedDate().isBefore(endDate))
                .map(reservation -> WrittenReviewInfo.of(reservation))
                .collect(Collectors.toList());
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

    private void deleteGradeRecord(Studycafe studycafe, Double gradeTotal) {
        studycafe.deleteGrade(gradeTotal);
    }

    private void deleteHashtagRecords(Long reviewId, Studycafe studycafe) {
        List<AccumHashtagHistory> AccumHashtags = accumHashtagHistoryRepository.findAllByStudycafe(studycafe);
        deleteAllHashtagRecord(reviewId, AccumHashtags);
    }

    private Member getMember(Review review) {
        return findReservationRecordByReviewId(review.getId()).getMember();
    }

    private Room getRoom(Review review) {
        return findReservationRecordByReviewId(review.getId()).getRoom();
    }

    private List<Review> getAllReviews(Long studycafeId) {
        return reviewRepository.findAllByStudycafeIdWithoutPage(studycafeId);
    }

    private Page<Review> getAllReviewsSorted(Long studycafeId, Pageable pageable) {
        return findAllByStudycafeId(studycafeId, pageable);
    }

    private Page<Review> getRoomReviewsSorted(Long studycafeId, Long roomId, Pageable pageable) {
        return findAllByRoomId(roomId, pageable);
    }

    private Studycafe findStudycafeById(Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
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

    private AccumHashtagHistory findAccumHashtagHistoryByName(String userHashtag) {
        return accumHashtagHistoryRepository.findByHashtagName(HashtagName.valueOf(userHashtag)).orElseThrow(() -> new NotFoundException(NOT_FOUND_HASHTAG));
    }

    private Page<ReservationRecord> findAllReservationRecordByMember(Member member, Pageable pageable) {
        return reservationRecordRepository.findAllByMember(pageable, member);
    }

    private void deleteAllHashtagRecord(Long reviewId, List<AccumHashtagHistory> studycafeAccumHashtag) {
        List<HashtagName> hashtagNames = hashtagRecordRepository.findAllByReviewId(reviewId);

        for (HashtagName hashtag : hashtagNames) {
            for (AccumHashtagHistory accumHashtagHistory : studycafeAccumHashtag) {
                if (hashtag.equals(accumHashtagHistory.getName())) {
                    accumHashtagHistory.subtractCount();
                }
            }
        }

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
        List<SubPhoto> photos = subPhotoRepository.findAllByReviewId(reviewId);
        List<String> reviewPhotos = new ArrayList<>();
        reviewPhotos.add(review.getPhoto());
        List<String> reviewSubPhoto = photos.stream().map(SubPhoto::getPath).collect(Collectors.toList());
        reviewPhotos.addAll(reviewSubPhoto);

        return reviewPhotos;
    }

    private void validateAvailableReviewTime(ReservationRecord reservation) {
        if (LocalDate.now().isBefore(reservation.getDate()) && LocalTime.now().isBefore(reservation.getEndTime())) {
            throw new BadRequestException(INVALID_WRITE_REVIEW_TIME);
        }
    }

    private void validateReservationStatus(ReservationRecord reservation) {
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BadRequestException(INVALID_RESERVATION_STATUS);
        }
    }

    private void updateHashtagRecord(List<String> hashtags, Review review, Studycafe studycafe) {
        for (String userHashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder()
                    .name(HashtagName.valueOf(userHashtag))
                    .build();
            review.addHashtagRecord(hashtagRecord);

            validateExistedAccumHashtag(studycafe, userHashtag);
            AccumHashtagHistory hashtagHistory = AccumHashtagHistory.builder().name(HashtagName.valueOf(userHashtag)).studycafe(studycafe).build();
            hashtagHistory.updateCount();
            studycafe.addAccumHashtagHistory(hashtagHistory);
        }
    }

    private void validateExistedAccumHashtag(Studycafe studycafe, String userHashtag) {
        if (accumHashtagHistoryRepository.existsByStudycafeAndHashtagName(studycafe,HashtagName.valueOf(userHashtag))) {
            AccumHashtagHistory hashtagHistory = findAccumHashtagHistoryByName(userHashtag);
            hashtagHistory.updateCount();
        }
    }
}