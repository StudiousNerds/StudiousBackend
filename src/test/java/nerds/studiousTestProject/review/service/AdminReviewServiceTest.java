package nerds.studiousTestProject.review.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import nerds.studiousTestProject.review.dto.manage.inquire.response.ReviewInfoResponse;
import nerds.studiousTestProject.review.dto.manage.modify.request.ModifyCommentRequest;
import nerds.studiousTestProject.review.dto.manage.register.request.RegisterCommentRequest;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import nerds.studiousTestProject.support.fixture.ReviewFixture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AdminReviewServiceTest {
    @InjectMocks
    AdminReviewService adminReviewService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    StudycafeRepository studycafeRepository;

    @Mock
    SubPhotoRepository subPhotoRepository;

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("현재 토큰의 회원이 studycafeId 경로 변수의 스터디 카페 주인이 아닌 경우 BadRequest 예외가 발생")
    public void 회원_스터디카페_불일치() throws Exception {

        // given
        Long studycafeId = 1L;
        Member member = DEFAULT_USER.생성();

        doReturn(Optional.of(member)).when(memberRepository).findById(member.getId());
        doReturn(false).when(studycafeRepository).existsByIdAndMember(studycafeId, member);

        // when
        assertThrows(NotFoundException.class, () -> {
            adminReviewService.getWrittenReviews(studycafeId, member.getId(), null, null);
        });

        // then

    }

    @Test
    @DisplayName("현재 관리자가 소유한 스터디카페의 모든 리뷰 조회")
    public void 스터디카페_모든_리뷰_조회() throws Exception {

        // given
        Long studycafeId = 1L;
        Member member = DEFAULT_USER.생성();

        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            reviews.add(ReviewFixture.TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        }
        Page<Review> pages = new PageImpl<>(reviews);

        doReturn(Optional.of(member)).when(memberRepository).findById(member.getId());
        doReturn(true).when(studycafeRepository).existsByIdAndMember(studycafeId, member);
        doReturn(pages).when(reviewRepository).getPagedReviewsByStudycafeId(studycafeId, null, null);

        // when
        List<ReviewInfoResponse> responses = adminReviewService.getWrittenReviews(studycafeId, member.getId(), null, null);

        // then
        assertThat(responses.size()).isEqualTo(30);
    }

    @Test
    @DisplayName("댓글 등록 시 댓글이 10자 미만이면 검증에 실패한다.")
    public void 댓글_등록_검증_실패() throws Exception {

        // given
        RegisterCommentRequest request = new RegisterCommentRequest();
        request.setComment("Hello");

        // when
        Set<ConstraintViolation<RegisterCommentRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.stream().anyMatch(
                error -> error.getMessage().equals("댓글은 최소 10자 이상이여야 합니다.")
        )).isTrue();
    }

    @Test
    @DisplayName("특정 리뷰에 댓글 등록")
    public void 댓글_등록() throws Exception {

        // given
        RegisterCommentRequest request = new RegisterCommentRequest();
        request.setComment("관리자 댓글 등록 테스트");

        Long reviewId = 1L;
        Review review = ReviewFixture.TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0);

        doReturn(Optional.of(review)).when(reviewRepository).findById(reviewId);

        // when
        adminReviewService.registerComment(reviewId, request);

        // then
        assertThat(review.getComment()).isEqualTo(request.getComment());
    }

    @Test
    @DisplayName("댓글 수정 시 댓글이 10자 미만이면 검증에 실패한다.")
    public void 댓글_수정_검증_실패() throws Exception {

        // given
        ModifyCommentRequest request = new ModifyCommentRequest();
        request.setComment("Hello");

        // when
        Set<ConstraintViolation<ModifyCommentRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.stream().anyMatch(
                error -> error.getMessage().equals("댓글은 최소 10자 이상이여야 합니다.")
        )).isTrue();
    }

    @Test
    @DisplayName("특정 리뷰의 댓글 수정")
    public void 댓글_수정() throws Exception {

        // given
        ModifyCommentRequest request = new ModifyCommentRequest();
        request.setComment("관리자 댓글 등록 테스트");

        Long reviewId = 1L;
        Review review = ReviewFixture.TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0);

        doReturn(Optional.of(review)).when(reviewRepository).findById(reviewId);

        // when
        adminReviewService.modifyComment(reviewId, request);

        // then
        assertThat(review.getComment()).isEqualTo(request.getComment());
    }

    @Test
    @DisplayName("특정 리뷰의 댓글 삭제")
    public void 댓글_삭제() throws Exception {

        // given
        Long reviewId = 1L;
        Review review = ReviewFixture.TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0);

        doReturn(Optional.of(review)).when(reviewRepository).findById(reviewId);

        // when
        adminReviewService.deleteComment(reviewId);

        // then
        assertNull(review.getComment());
    }
}