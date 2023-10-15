package nerds.studiousTestProject.hashtag.repository;

import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.support.fixture.HashtagFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static nerds.studiousTestProject.support.EntitySaveProvider.리뷰_저장;
import static nerds.studiousTestProject.support.fixture.ReviewFixture.TODAY_COMMENTED_REVIEW;

@RepositoryTest
class HashtagRecordRepositoryTest {

    @Autowired
    private HashtagRecordRepository hashtagRecordRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName(value = "리뷰 id를 통해 모든 해시태그 내역을 삭제할 수 있다.")
    void deleteAllByReviewId() {
        // given
        Review review = 리뷰_저장(TODAY_COMMENTED_REVIEW.평점_정보_생성(1, 1, 1, 1.0));
        HashtagRecord hashtag = hashtagRecordRepository.save(HashtagFixture.COST_EFFECTIVE_HASHTAG.리뷰_생성(review, 1L));
        review.addHashtagRecord(hashtag);
        List<Long> reviewId = new ArrayList<>();
        reviewId.add(1L);
        // when
        Assertions.assertThat(review.getHashtagRecords().contains(HashtagFixture.COST_EFFECTIVE_HASHTAG));
        review.getHashtagRecords().removeAll(review.getHashtagRecords());
        hashtagRecordRepository.deleteAllByReviewId(1L);
        Review foundreview = reviewRepository.findById(1L).get();
        List<HashtagRecord> hashtagList = hashtagRecordRepository.findAll();
        // then
        Assertions.assertThat(hashtagList).isEmpty();
        Assertions.assertThat(foundreview.getHashtagRecords()).isEmpty();
    }
}