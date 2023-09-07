package nerds.studiousTestProject.hashtag.repository;

import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.support.fixture.HashtagFixture;
import nerds.studiousTestProject.support.fixture.ReviewFixture;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@RepositoryTest
class HashtagRepositoryTest {
    @Autowired
    private HashtagRecordRepository hashtagRecordRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void deleteAllByReviewId() {
        // given
        HashtagRecord hashtag = hashtagRecordRepository.save(HashtagFixture.COST_EFFECTIVE_HASHTAG.생성(1L));
        Review save = reviewRepository.save(ReviewFixture.TODAY_COMMENTED_REVIEW.기본_정보_생성(1L));
        save.addHashtagRecord(hashtag);
        List<Long> reviewId = new ArrayList<>();
        reviewId.add(1L);
        // when
        save.getHashtagRecords().removeAll(save.getHashtagRecords());
        hashtagRecordRepository.deleteAllByReviewId(1L);
        Review review = reviewRepository.findById(1L).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_REVIEW));
        List<HashtagRecord> hashtagList = hashtagRecordRepository.findAll();
        // then
        Assertions.assertThat(hashtagList.size()).isEqualTo(0);
        Assertions.assertThat(review.getHashtagRecords()).isEmpty();
    }
}