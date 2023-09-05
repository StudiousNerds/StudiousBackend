package nerds.studiousTestProject.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.review.dto.manage.inquire.ReviewInfoResponse;
import nerds.studiousTestProject.review.dto.manage.modify.ModifyCommentRequest;
import nerds.studiousTestProject.review.dto.manage.register.RegisterCommentRequest;
import nerds.studiousTestProject.review.dto.modify.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.register.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.available.response.AvailableReviewResponse;
import nerds.studiousTestProject.review.dto.delete.response.DeleteReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.FindReviewSortedResponse;
import nerds.studiousTestProject.review.dto.modify.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.written.response.WrittenReviewResponse;
import nerds.studiousTestProject.review.service.AdminReviewService;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.annotation.Secured;
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
    private final AdminReviewService adminReviewService;

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
    public FindReviewSortedResponse findAllReviews(@PathVariable("studycafeId") Long studycafeId, Pageable pageable) {
        return reviewService.findAllReviews(studycafeId, pageable);
    }

    @GetMapping("/studycafes/{studycafeId}/rooms/{roomId}/reviews")
    public FindReviewSortedResponse findRoomReviews(@PathVariable("studycafeId") Long studycafeId, @PathVariable("roomId") Long roomId, Pageable pageable) {
        return reviewService.findRoomReviews(studycafeId, roomId, pageable);
    }

    @GetMapping("/studycafes/{studycafeId}/reviews/managements")
    @Secured(value = MemberRole.ROLES.ADMIN)
    public List<ReviewInfoResponse> inquireStudycafeReviews(@PathVariable("studycafeId") Long studycafeId, @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam Integer page) {
        return adminReviewService.getWrittenReviews(studycafeId, accessToken, PageRequestConverter.of(page, 3));
    }

    @PostMapping("/reviews/managements/{reviewId}")
    @Secured(value = MemberRole.ROLES.ADMIN)
    public void registerComment(@PathVariable("reviewId") Long reviewId, @RequestBody @Valid RegisterCommentRequest registerCommentRequest) {
        adminReviewService.registerComment(reviewId, registerCommentRequest);
    }

    @PatchMapping("/reviews/managements/{reviewId}")
    @Secured(value = MemberRole.ROLES.ADMIN)
    public void modifyComment(@PathVariable("reviewId") Long reviewId, @RequestBody @Valid ModifyCommentRequest modifyCommentRequest) {
        adminReviewService.modifyComment(reviewId, modifyCommentRequest);
    }

    @DeleteMapping("/reviews/managements/{reviewId}")
    @Secured(value = MemberRole.ROLES.ADMIN)
    public void deleteComment(@PathVariable("reviewId") Long reviewId) {
        adminReviewService.deleteComment(reviewId);
    }
}
