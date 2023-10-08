package nerds.studiousTestProject.hashtag.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HashtagRecordRepository extends JpaRepository<HashtagRecord, Long> {
    void deleteAllByReviewId(Long reviewId);

    @Query(value = "select h.name from HashtagRecord as h join fetch ReservationRecord as r " +
            "where r.room.studycafe.id = :studycafeId " +
            "group by h.name " +
            "order by count(h.id) desc")
    List<HashtagName> findHashtagRecordByStudycafeId(@Param("studycafeId") Long studycafeId);
}
