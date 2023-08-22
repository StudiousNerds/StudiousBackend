package nerds.studiousTestProject.hashtag.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface HashtagRecordRepository extends JpaRepository<HashtagRecord, Long> {
    void deleteAllByReviewId(Long reviewId);

//    @Query(value = "select h.name from HashtagRecord as h " +
//            "where h.review.reservationRecord.room.studycafe.id = :studycafeId " +
//            "group by h.name " +
//            "order by count(h.id) desc")
//    List<HashtagName> findHashtagRecordByStudycafeId(@Param("studycafeId") Long studycafeId);
}
