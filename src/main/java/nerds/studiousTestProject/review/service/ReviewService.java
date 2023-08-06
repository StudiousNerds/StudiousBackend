package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.review.dto.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.response.FindReviewResponse;
import nerds.studiousTestProject.review.dto.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.response.RegisterReviewResponse;
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

import static nerds.studiousTestProject.common.exception.ErrorCode.*;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SubPhotoService subPhotoService;
    private final StudycafeRepository studycafeRepository;
    private final RoomRepository roomRepository;
    private final ReservationRecordRepository reservationRecordRepository;

    private List<Review> getReviewList(Long studycafeId) {
        List<ReservationRecord> recordList = findAllReservation(studycafeId);
        List<Review> reviewList = new ArrayList<>();
        for (ReservationRecord r : recordList) {
            List<Review> reviews = reviewRepository.findAllById(r.getId());
            for (int i = 0; i < reviews.size(); i++) {
                reviewList.add(reviews.get(i));
            }
        }
        return reviewList;
    }

    @Transactional
    public RegisterReviewResponse registerReview(RegisterReviewRequest registerReviewRequest){
        Grade grade = Grade.builder().cleanliness(registerReviewRequest.getCleanliness())
                .deafening(registerReviewRequest.getDeafening())
                .fixturesStatus(registerReviewRequest.getFixtureStatus())
                .isRecommended(registerReviewRequest.getIsRecommend())
                .build();
        grade.updateTotal(getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        Review review = Review.builder()
                .grade(grade)
                .createdDate(LocalDate.now())
                .detail(registerReviewRequest.getDetail())
                .build();
        reviewRepository.save(review);

        List<String> photos = Arrays.stream(registerReviewRequest.getPhotos()).toList();
        for (String photo : photos) {
            SubPhoto subPhoto = SubPhoto.builder().review(review).url(photo).build();
            subPhotoService.savePhoto(subPhoto);
        }

        List<String> hashtags = Arrays.stream(registerReviewRequest.getHashtags()).toList();
        Studycafe studycafe = studycafeRepository.findById(registerReviewRequest.getCafeId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        for (String hashtag : hashtags) {
            HashtagRecord hashtagRecord = HashtagRecord.builder()
                    .name(HashtagName.valueOf(hashtag))
                    .build();
            hashtagRecord.addCount(1);
            studycafe.addHashtagRecord(hashtagRecord);
        }

        Double avgGrade = getAvgGrade(studycafe.getId());
        studycafe.addTotalGrade(avgGrade);

        return RegisterReviewResponse.builder().reviewId(review.getId()).createdAt(LocalDate.now()).build();
    }

    public ModifyReviewResponse modifyReview(Long reviewId, ModifyReviewRequest modifyReviewRequest) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVEIW));
        Studycafe studycafe = studycafeRepository.findById(modifyReviewRequest.getCafeId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        Grade grade = review.getGrade();

        grade.updateGrade(modifyReviewRequest.getCleanliness(),
                modifyReviewRequest.getDeafening(),
                modifyReviewRequest.getFixtureStatus(),
                modifyReviewRequest.getIsRecommend(),
                getTotal(grade.getCleanliness(), grade.getDeafening(), grade.getFixturesStatus()));

        Double avgGrade = getAvgGrade(studycafe.getId());
        studycafe.addTotalGrade(avgGrade);

        /**
         * 해시태그를 기존의 해시태그에 있는 개수를 1개씩 뺀다음에, 다시 받아온 해시태그의 개수를 증가시키고 없었던 경우에는 새롭게 추가
         */
        List<HashtagRecord> hashtagRecords = studycafe.getHashtagRecords();
        List<String> hashtags = Arrays.stream(modifyReviewRequest.getHashtags()).toList();
        for (HashtagRecord hashtag : hashtagRecords) {
            hashtag.subtractCount(1);
        }

        // 이 부분 depth가 맘에 좀 걸려서 혹시 좋은 방법 있으시면 조언 부탁드립니다..ㅜㅜㅜㅜ
        for (String userHashtag : hashtags) {
            for(HashtagRecord hashtagRecord : hashtagRecords) {
                if (hashtagRecord.getName().toString() == userHashtag) {
                    hashtagRecord.addCount(1);
                } else {
                    HashtagRecord hashtag = HashtagRecord.builder()
                            .name(HashtagName.valueOf(userHashtag))
                            .build();
                    hashtagRecord.addCount(1);
                    studycafe.addHashtagRecord(hashtagRecord);
                }
            }
        }

        /**
         * 사진은 같은 것들이 있으면 있는 것들을 삭제하고, 다시 받아온 url로 저장을 한다.
         */
        subPhotoService.removePhoto(reviewId);
        List<String> photos = Arrays.stream(modifyReviewRequest.getPhotos()).toList();
        for (String photo : photos) {
            SubPhoto subPhoto = SubPhoto.builder().review(review).url(photo).build();
            subPhotoService.savePhoto(subPhoto);
        }

        review.updateDetail(modifyReviewRequest.getDetail());

        return ModifyReviewResponse.builder().reviewId(reviewId).modifiedAt(LocalDate.now()).build();
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

    public List<ReservationRecord> findAllReservation(Long studycafeId){
        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);
        List<ReservationRecord> reservationRecordList = new ArrayList<>();
        for (Room r : roomList){
            List<ReservationRecord> reservationRecords = reservationRecordRepository.findAllByRoomId(r.getId());
            for (int i = 0; i < reservationRecords.size(); i++) {
                reservationRecordList.add(reservationRecords.get(i));
            }
        }
        return reservationRecordList;
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
        return (cleanliness + deafening + fixtureStatus) / 3.0;
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
}
