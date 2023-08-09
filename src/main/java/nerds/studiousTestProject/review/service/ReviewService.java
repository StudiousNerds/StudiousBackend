package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.repository.HashtagRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.service.member.MemberService;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import nerds.studiousTestProject.review.dto.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.response.AvailableReviewResponse;
import nerds.studiousTestProject.review.dto.response.DeleteReviewResponse;
import nerds.studiousTestProject.review.dto.response.FindReviewResponse;
import nerds.studiousTestProject.review.dto.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.response.WrittenReviewResponse;
import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final MemberService memberService;
    private final StudycafeRepository studycafeRepository;
    private final HashtagRepository hashtagRepository;
    private final RoomRepository roomRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    public final Double GRADE_COUNT = 3.0;


    @Transactional
    public RegisterReviewResponse registerReview(RegisterReviewRequest registerReviewRequest){
        Studycafe studycafe = findByStudycafeId(registerReviewRequest.getCafeId());

        Grade grade = Grade.builder().cleanliness(registerReviewRequest.getCleanliness())
                .deafening(registerReviewRequest.getDeafening())
                .fixturesStatus(registerReviewRequest.getFixtureStatus())
                .isRecommended(registerReviewRequest.getIsRecommend())
                .build();
        grade.updateTotal(getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        Review review = Review.builder()
                .createdDate(LocalDate.now())
                .detail(registerReviewRequest.getDetail())
                .build();
        reviewRepository.save(review);
        review.addGrade(grade);

        ReservationRecord reservationRecord = reservationRecordService.findById(registerReviewRequest.getReservationId());
        reservationRecord.addReview(review);

        List<String> hashtags = Arrays.stream(registerReviewRequest.getHashtags()).toList();
        for (String userHashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder().count(1)
                    .studycafe(studycafe)
                    .review(review)
                    .name(HashtagName.valueOf(userHashtag))
                    .build();
            review.addHashtagRecord(hashtagRecord);
        }

        List<String> photos = Arrays.stream(registerReviewRequest.getPhotos()).toList();
        for (String photo : photos) {
            SubPhoto subPhoto = SubPhoto.builder().review(review).url(photo).build();
            subPhotoService.savePhoto(subPhoto);
        }

        Double avgGrade = getAvgGrade(studycafe.getId());
        studycafe.addTotalGrade(avgGrade);

        return RegisterReviewResponse.builder().reviewId(review.getId()).createdAt(LocalDate.now()).build();
    }

    public ModifyReviewResponse modifyReview(Long reviewId, ModifyReviewRequest modifyReviewRequest) {
        Review review = findById(reviewId);
        Studycafe studycafe = findByStudycafeId(modifyReviewRequest.getCafeId());

        Grade grade = review.getGrade();
        grade.updateGrade(modifyReviewRequest.getCleanliness(),
                modifyReviewRequest.getDeafening(),
                modifyReviewRequest.getFixtureStatus(),
                modifyReviewRequest.getIsRecommend(),
                getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        // 리뷰가 수정되면서 grade가 바뀌면서 총점이 변경되는 경우가 있을 수 있으니, 스터디카페의 totalGrade 값 업데이트
        Double avgGrade = getAvgGrade(studycafe.getId());
        studycafe.addTotalGrade(avgGrade);

        hashtagRepository.deleteAllByReviewId(reviewId);
        List<String> hashtags = Arrays.stream(modifyReviewRequest.getHashtags()).toList();
        for (String userHashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder().count(1)
                    .studycafe(studycafe)
                    .review(review)
                    .name(HashtagName.valueOf(userHashtag))
                    .build();
            review.addHashtagRecord(hashtagRecord);
        }

        // 사진은 리뷰id를 통해 삭제하고, 다시 받아온 url로 저장을 한다.
        subPhotoService.removeAllPhotos(reviewId);
        List<String> photos = Arrays.stream(modifyReviewRequest.getPhotos()).toList();
        for (String photo : photos) {
            SubPhoto subPhoto = SubPhoto.builder().review(review).url(photo).build();
            subPhotoService.savePhoto(subPhoto);
        }

        review.updateDetail(modifyReviewRequest.getDetail());

        return ModifyReviewResponse.builder().reviewId(reviewId).modifiedAt(LocalDate.now()).build();
    }

    public DeleteReviewResponse deleteReview(Long reviewId, Long studycafeId) {
        hashtagRepository.deleteAllByReviewId(reviewId);
        subPhotoService.removeAllPhotos(reviewId);
        reviewRepository.deleteById(reviewId);

        // 리뷰가 삭제되면서 등급도 사라졌기 때문에 새롭게 스터디카페의 totalGrade값을 업데이트 해줌
        Studycafe studycafe = findByStudycafeId(studycafeId);
        Double avgGrade = getAvgGrade(studycafe.getId());
        studycafe.addTotalGrade(avgGrade);

        return DeleteReviewResponse.builder().reviewId(reviewId).deletedAt(LocalDate.now()).build();
    }

    public List<ReservationRecord> findAllReservation(Long studycafeId){
        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);
        List<ReservationRecord> reservationRecordList = new ArrayList<>();
        for (Room room : roomList){
            List<ReservationRecord> reservationRecords = reservationRecordRepository.findAllByRoomId(room.getId());
            for (int i = 0; i < reservationRecords.size(); i++) {
                reservationRecordList.add(reservationRecords.get(i));
            }
        }
        return reservationRecordList;
    }

    private List<Review> getReviewList(Long studycafeId) {
        List<ReservationRecord> recordList = findAllReservation(studycafeId);
        List<Review> reviewList = new ArrayList<>();
        for (ReservationRecord reservationRecord : recordList) {
            List<Review> reviews = reviewRepository.findAllByReservationRecordId(reservationRecord.getId());
            for (int i = 0; i < reviews.size(); i++) {
                reviewList.add(reviews.get(i));
            }
        }
        return reviewList;
    }

    public List<FindReviewResponse> findAllReviews(Long id) {
        List<Review> reviewList = getReviewList(id);
        List<FindReviewResponse> reviewResponses = new ArrayList<>();

        for (Review review : reviewList) {
            reviewResponses.add(FindReviewResponse.builder()
                    .grade(review.getGrade().getTotal())
                    .email(review.getReservationRecord().getMember().getNickname())
                    .detail(review.getDetail())
                    .date(review.getCreatedDate())
                    .photos(subPhotoService.findReviewPhotos(review.getId()))
                    .build());
        }

        return reviewResponses;
    }

    /**
     * 리뷰 작성 가능한 내역을 조회하는 메소드
     */
    public List<AvailableReviewResponse> findAvailableReviews(String accessToken) {
        List<ReservationRecord> reservationRecordList = getReservationRecords(accessToken);

        return  reservationRecordList.stream()
                .filter(reservationRecord -> reservationRecord.getReview() == null)
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
    public List<WrittenReviewResponse> findWrittenReviews(String accessToken) {
        List<ReservationRecord> reservationRecordList = getReservationRecords(accessToken);

        return reservationRecordList.stream()
                .filter(reservationRecord -> reservationRecord.getReview() != null)
                .map(reservationRecord -> WrittenReviewResponse.builder()
                        .reservationId(reservationRecord.getId())
                        .studycafeId(reservationRecord.getRoom().getStudycafe().getId())
                        .studycafeName(reservationRecord.getRoom().getStudycafe().getName())
                        .studycafePhoto(reservationRecord.getRoom().getStudycafe().getPhoto())
                        .roomName(reservationRecord.getRoom().getName())
                        .date(reservationRecord.getDate())
                        .writeDate(reservationRecord.getReview().getCreatedDate())
                        .cleanliness(reservationRecord.getReview().getGrade().getCleanliness())
                        .deafening(reservationRecord.getReview().getGrade().getDeafening())
                        .fixtureStatus(reservationRecord.getReview().getGrade().getFixturesStatus())
                        .reviewPhoto(subPhotoService.findReviewPhotos(reservationRecord.getReview().getId())[0])
                        .detail(reservationRecord.getReview().getDetail())
                        .build())
                .collect(Collectors.toList());
    }

    public Integer getAvgCleanliness(Long id){
        List<Review> reviewList = getReviewList(id);
        Integer count = 0, sum = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getCleanliness();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgDeafening(Long id) {
        List<Review> reviewList = getReviewList(id);
        Integer count = 0, sum = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getDeafening();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgFixturesStatus(Long id) {
        List<Review> reviewList = getReviewList(id);
        Integer count = 0, sum = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getFixturesStatus();
            count++;
        }

        return sum / count;
    }

    public Integer getAvgRecommendation(Long id) {
        List<Review> reviewList = getReviewList(id);
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

    public Double getAvgGrade(Long id) {
        List<Review> reviewList = getReviewList(id);
        Integer count = 0;
        Double sum = 0.0;

        for (Review review : reviewList){
            sum += review.getGrade().getTotal();
            count++;
        }
        return  sum/ count;
    }

    private List<ReservationRecord> getReservationRecords(String accessToken) {
        Member member = memberService.getMemberFromAccessToken(accessToken);
        List<ReservationRecord> reservationRecordList = reservationRecordService.findAllByMemberId(member.getId());
        return reservationRecordList;
    }

    private Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVEIW));
    }

    private Studycafe findByStudycafeId(Long studycafeId) {
        return studycafeRepository.findById(studycafeId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }
}
