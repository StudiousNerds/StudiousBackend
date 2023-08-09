package nerds.studiousTestProject.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.review.dto.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.response.AvailableReviewResponse;
import nerds.studiousTestProject.review.dto.response.DeleteReviewResponse;
import nerds.studiousTestProject.review.dto.response.FindReviewResponse;
import nerds.studiousTestProject.review.dto.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.response.WrittenReviewResponse;
import nerds.studiousTestProject.review.service.ReviewService;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Slf4j
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public RegisterReviewResponse registerReview(@RequestBody @Valid RegisterReviewRequest registerReviewRequest) {
        return reviewService.registerReview(registerReviewRequest);
    }

    @PatchMapping("/reviews/{reviewId}")
    public ModifyReviewResponse modifyReview(@PathVariable("reviewId") Long reviewId, @RequestBody @Valid ModifyReviewRequest modifyReviewRequest) {
        return reviewService.modifyReview(reviewId, modifyReviewRequest);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public DeleteReviewResponse deleteReview(@PathVariable("reviewId") Long reviewId, @RequestParam("studycafe") Long studycafeId) {
        return reviewService.deleteReview(reviewId, studycafeId);
    }

    @GetMapping("/reviews/available")
    public List<AvailableReviewResponse> InquireAvailableReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return reviewService.findAvailableReviews(accessToken);
    }

    @GetMapping("/reviews")
    public List<WrittenReviewResponse> InquireWrittenReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return reviewService.findWrittenReviews(accessToken);
    }

    @GetMapping("/studycafes/{cafeId}/reviews")
    public List<FindReviewResponse> findAllReviews(@PathVariable("cafeId") Long studycafeId) {
        return reviewService.findAllReviews(studycafeId);
    }

    @GetMapping("/studycafes/{cafeId}/rooms/{roomId}/reviews")
    public List<FindReviewResponse> findRoomReviews(@PathVariable("cafeId") Long studycafeId, @PathVariable("roomId") Long roomId) {
        return reviewService.findRoomReviews(studycafeId, roomId);
    }

    @GetMapping("/studycafes/{cafeId}/reviews/sorted")
    public List<FindReviewResponse> findAllReviewsSorted(@PathVariable("cafeId") Long studycafeId, @RequestParam("sort") String sortType) {
        return reviewService.findAllReviewsSorted(studycafeId, sortType);
    }
}
