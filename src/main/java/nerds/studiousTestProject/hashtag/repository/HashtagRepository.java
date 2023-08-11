package nerds.studiousTestProject.hashtag.repository;

import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<HashtagRecord, Long> {
    void deleteAllByReviewId(Long reviewId);
}
