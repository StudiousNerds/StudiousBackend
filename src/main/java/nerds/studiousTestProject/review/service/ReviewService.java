package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.review.dto.FindReviewResponse;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SubPhotoService subPhotoService;

    private List<Review> getReviewList(Long id) {
        List<Review> reviewList = reviewRepository.findAllById(id);
        return reviewList;
    }

    public List<FindReviewResponse> findAllReviews(Long id){
        List<Review> reviewList = getReviewList(id);
        List<FindReviewResponse> reviewResponses = new ArrayList<>();

        for (Review review :reviewList) {
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

    public Integer getAvgCleanliness(Long id){
        List<Review> reviewList = getReviewList(id);
        Integer count = 0, sum = 0, result = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getCleanliness();
            count++;
        }

        return result = sum / count;
    }

    public Integer getAvgDeafening(Long id){
        List<Review> reviewList = getReviewList(id);
        Integer count = 0, sum = 0, result = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getDeafening();
            count++;
        }

        return result = sum / count;
    }

    public Integer getAvgFixturesStatus(Long id){
        List<Review> reviewList = getReviewList(id);
        Integer count = 0, sum = 0, result = 0;

        for (Review review : reviewList){
            sum += review.getGrade().getFixturesStatus();
            count++;
        }

        return result = sum / count;
    }

    public Integer getAvgRecommendation(Long id){
        List<Review> reviewList = getReviewList(id);
        Integer recommend = 0, count = 0, result = 0;

        for (Review review : reviewList){
            recommend += review.getGrade().getFixturesStatus();
            count++;
        }

        return result = recommend / count * 100;
    }
}
