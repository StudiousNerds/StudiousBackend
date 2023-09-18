package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import nerds.studiousTestProject.review.dto.manage.inquire.request.AdminReviewType;
import nerds.studiousTestProject.review.dto.manage.inquire.response.ReviewInfoResponse;
import nerds.studiousTestProject.review.dto.manage.modify.request.ModifyCommentRequest;
import nerds.studiousTestProject.review.dto.manage.register.request.RegisterCommentRequest;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.MISMATCH_MEMBER_AND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_REVIEW;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
public class AdminReviewService {
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final StudycafeRepository studycafeRepository;
    private final SubPhotoRepository subPhotoRepository;

    public List<ReviewInfoResponse> getWrittenReviews(Long studycafeId, Long memberId, AdminReviewType reviewType, Pageable pageable) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));

        if (!matchStudycafeAndMember(studycafeId, member)) {
            throw new NotFoundException(MISMATCH_MEMBER_AND_STUDYCAFE);
        }

        List<Review> reviews = reviewRepository.getPagedReviewsByStudycafeId(studycafeId, reviewType, pageable).getContent();
        return reviews.stream().map(r -> ReviewInfoResponse.from(r, subPhotoRepository.findAllByReviewId(r.getId()))).toList();
    }

    private boolean matchStudycafeAndMember(Long studycafeId, Member member) {
        return studycafeRepository.existsByIdAndMember(studycafeId, member);
    }

    @Transactional
    public void registerComment(Long reviewId, RegisterCommentRequest registerCommentRequest) {
        getReview(reviewId).updateComment(registerCommentRequest.getComment());
    }

    @Transactional
    public void modifyComment(Long reviewId, ModifyCommentRequest modifyCommentRequest) {
        getReview(reviewId).updateComment(modifyCommentRequest.getComment());
    }

    @Transactional
    public void deleteComment(Long reviewId) {
        getReview(reviewId).deleteComment();
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW));
    }
}
