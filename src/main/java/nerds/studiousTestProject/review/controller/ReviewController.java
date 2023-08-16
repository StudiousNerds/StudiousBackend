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
import org.springframework.data.domain.Pageable;
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

import java.time.LocalDate;
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
    public DeleteReviewResponse deleteReview(@PathVariable("reviewId") Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/reviews/available")
    public List<AvailableReviewResponse> InquireAvailableReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return reviewService.findAvailableReviews(accessToken);
    }

    @GetMapping("/reviews")
    public List<WrittenReviewResponse> InquireWrittenReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                            @RequestParam("startDate") LocalDate startDate,
                                                            @RequestParam("endDate") LocalDate endDate) {
        return reviewService.findWrittenReviews(accessToken, startDate, endDate);
    }

    @GetMapping("/studycafes/{studycafeId}/reviews")
    public List<FindReviewResponse> findAllReviews(@PathVariable("studycafeId") Long studycafeId, Pageable pageable) {
        return reviewService.findAllReviews(studycafeId, pageable);
    }

    @GetMapping("/studycafes/{studycafeId}/rooms/{roomId}/reviews")
    public List<FindReviewResponse> findRoomReviews(@PathVariable("studycafeId") Long studycafeId, @PathVariable("roomId") Long roomId, Pageable pageable) {
        return reviewService.findRoomReviews(studycafeId, roomId, pageable);
    }
}
