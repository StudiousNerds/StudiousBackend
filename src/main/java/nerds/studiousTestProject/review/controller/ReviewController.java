package nerds.studiousTestProject.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.review.dto.available.response.AvailableReviewResponse;
import nerds.studiousTestProject.review.dto.modify.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.register.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.delete.response.DeleteReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.FindReviewSortedResponse;
import nerds.studiousTestProject.review.dto.modify.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.written.response.WrittenReviewResponse;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public RegisterReviewResponse registerReview(@RequestPart("registerReviewRequest") @Valid RegisterReviewRequest registerReviewRequest,
                                                 @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return reviewService.registerReview(registerReviewRequest, files);
    }

    @PatchMapping("/reviews/{reviewId}")
    public ModifyReviewResponse modifyReview(@PathVariable("reviewId") Long reviewId, @RequestPart("modifyReviewRequest") @Valid ModifyReviewRequest modifyReviewRequest,
                                             @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return reviewService.modifyReview(reviewId, modifyReviewRequest, files);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public DeleteReviewResponse deleteReview(@PathVariable("reviewId") Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/reviews/available")
    public AvailableReviewResponse InquireAvailableReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam Integer page) {
        return reviewService.findAvailableReviews(accessToken, PageRequestConverter.of(page, 5));
    }

    @GetMapping("/reviews")
    public WrittenReviewResponse InquireWrittenReview(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                      @RequestParam("startDate") LocalDate startDate,
                                                      @RequestParam("endDate") LocalDate endDate,
                                                      @RequestParam Integer page) {
        return reviewService.findWrittenReviews(accessToken, startDate, endDate, PageRequestConverter.of(page, 5));
    }

    @GetMapping("/studycafes/{studycafeId}/reviews")
    public FindReviewSortedResponse findAllReviews(@PathVariable("studycafeId") Long studycafeId, Pageable pageable) {
        return reviewService.findAllReviews(studycafeId, pageable);
    }

    @GetMapping("/studycafes/{studycafeId}/rooms/{roomId}/reviews")
    public FindReviewSortedResponse findRoomReviews(@PathVariable("studycafeId") Long studycafeId, @PathVariable("roomId") Long roomId, Pageable pageable) {
        return reviewService.findRoomReviews(studycafeId, roomId, pageable);
    }
}
