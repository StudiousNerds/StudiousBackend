package nerds.studiousTestProject.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.review.dto.available.response.AvailableReviewResponse;
import nerds.studiousTestProject.review.dto.delete.response.DeleteReviewResponse;
import nerds.studiousTestProject.review.dto.find.response.FindReviewSortedResponse;
import nerds.studiousTestProject.review.dto.manage.inquire.request.AdminReviewSortType;
import nerds.studiousTestProject.review.dto.manage.inquire.request.AdminReviewType;
import nerds.studiousTestProject.review.dto.manage.inquire.response.ReviewInfoResponse;
import nerds.studiousTestProject.review.dto.manage.modify.request.ModifyCommentRequest;
import nerds.studiousTestProject.review.dto.manage.register.request.RegisterCommentRequest;
import nerds.studiousTestProject.review.dto.modify.request.ModifyReviewRequest;
import nerds.studiousTestProject.review.dto.modify.response.ModifyReviewResponse;
import nerds.studiousTestProject.review.dto.register.request.RegisterReviewRequest;
import nerds.studiousTestProject.review.dto.register.response.RegisterReviewResponse;
import nerds.studiousTestProject.review.dto.written.response.WrittenReviewResponse;
import nerds.studiousTestProject.review.service.AdminReviewService;
import nerds.studiousTestProject.review.service.ReviewService;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private static final int REVIEW_INQUIRE_SIZE = 5;
    private static final int ADMIN_REVIEW_INQUIRE_SIZE = 3;

    @PostMapping("/mypage/reviews")
    public RegisterReviewResponse register(@RequestPart("registerReviewRequest") @Valid RegisterReviewRequest registerReviewRequest,
                                           @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return reviewService.register(registerReviewRequest, files);
    }

    @PatchMapping("/mypage/reviews/{reviewId}")
    public ModifyReviewResponse modifyReview(@PathVariable("reviewId") Long reviewId, @RequestPart("modifyReviewRequest") @Valid ModifyReviewRequest modifyReviewRequest,
                                             @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return reviewService.modifyReview(reviewId, modifyReviewRequest, files);
    }

    @DeleteMapping("/mypage/reviews/{reviewId}")
    public DeleteReviewResponse deleteReview(@PathVariable("reviewId") Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @GetMapping("/mypage/reviews/available")
    public AvailableReviewResponse InquireAvailableReview(@LoggedInMember Long memberId, @RequestParam Integer page) {
        return reviewService.findAvailableReviews(memberId, PageRequestConverter.of(page, REVIEW_INQUIRE_SIZE));
    }

    @GetMapping("/mypage/reviews")
    public WrittenReviewResponse InquireWrittenReview(
            @LoggedInMember Long memberId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam Integer page) {
        return reviewService.findWrittenReviews(memberId, startDate, endDate, PageRequestConverter.of(page, REVIEW_INQUIRE_SIZE));
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
    public List<ReviewInfoResponse> inquireStudycafeReviews(
            @LoggedInMember Long memberId,
            @PathVariable("studycafeId") Long studycafeId,
            @RequestParam Integer page,
            @RequestParam(required = false, defaultValue = AdminReviewSortType.Names.CREATED_DATE_DESC) AdminReviewSortType sortType,
            @RequestParam(required = false) AdminReviewType reviewType
            ) {
        return adminReviewService.getWrittenReviews(studycafeId, memberId, reviewType, PageRequestConverter.of(page, ADMIN_REVIEW_INQUIRE_SIZE, sortType.getSort()));
    }

    @PostMapping("/reviews/managements/{reviewId}")
    public void registerComment(@PathVariable("reviewId") Long reviewId, @RequestBody @Valid RegisterCommentRequest registerCommentRequest) {
        adminReviewService.registerComment(reviewId, registerCommentRequest);
    }

    @PatchMapping("/reviews/managements/{reviewId}")
    public void modifyComment(@PathVariable("reviewId") Long reviewId, @RequestBody @Valid ModifyCommentRequest modifyCommentRequest) {
        adminReviewService.modifyComment(reviewId, modifyCommentRequest);
    }

    @DeleteMapping("/reviews/managements/{reviewId}")
    public void deleteComment(@PathVariable("reviewId") Long reviewId) {
        adminReviewService.deleteComment(reviewId);
    }
}
