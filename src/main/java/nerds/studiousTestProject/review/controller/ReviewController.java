package nerds.studiousTestProject.review.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.review.dto.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.service.ReviewService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping()
    public RegisterReviewResponse registerReview(@RequestBody RegisterReviewRequest registerReviewRequest) {
        return reviewService.registerReview(registerReviewRequest);
    }

    @PatchMapping("/{reviewId}")
    public ModifyReviewResponse modifyReview(@PathVariable("reviewId") Long reviewId, @RequestBody ModifyReviewRequest modifyReviewRequest) {
        return reviewService.modifyReview(reviewId, modifyReviewRequest);
    }
}
