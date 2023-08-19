//package nerds.studiousTestProject.review.service;
//
//import nerds.studiousTestProject.hashtag.entity.HashtagName;
//import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
//import nerds.studiousTestProject.photo.service.SubPhotoService;
//import nerds.studiousTestProject.review.dto.request.RegisterReviewRequest;
//import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
//import nerds.studiousTestProject.review.repository.ReviewRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceTest {
//
//    @InjectMocks
//    private ReviewService reviewService;
//    @InjectMocks
//    private SubPhotoService subPhotoService;
//
//    @Mock
//    private ReviewRepository reviewRepository;
//    @Mock
//    private SubPhotoRepository subPhotoRepository;
//
//    @Test
//    void registerReview() {
//        // given
//        String[] hashtags = new String[5];
//        hashtags[0] = HashtagName.ACCESS.toString();
//
//        String[] photos = new String[8];
//        photos[0] = "www.review.com";
//
//        RegisterReviewRequest request = RegisterReviewRequest.builder()
//                .cafeId(1L)
//                .reservationId(1L)
//                .cleanliness(3)
//                .deafening(3)
//                .fixtureStatus(5)
//                .isRecommend(true)
//                .hashtags(hashtags)
//                .photos(photos)
//                .detail("좋음")
//                .build();
//
//        RegisterReviewResponse response1 = RegisterReviewResponse.builder().reviewId(1L).createdAt(LocalDate.now()).build();
//        //
//
//        // when
//        RegisterReviewResponse response = reviewService.registerReview(request);
//        // then
//        Assertions.assertThat(response).isEqualTo(response1);
//    }
//}