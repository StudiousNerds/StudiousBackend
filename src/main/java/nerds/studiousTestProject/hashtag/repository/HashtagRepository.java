package nerds.studiousTestProject.hashtag.repository;

import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashtagRepository extends JpaRepository<HashtagRecord, Long> {
    void deleteAllByReviewId(Long reviewId);
}