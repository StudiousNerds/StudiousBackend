package nerds.studiousTestProject.hashtag.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.hashtag.entity.AccumHashtagHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccumHashtagHistoryRepository extends JpaRepository<AccumHashtagHistory, Long> {
    @Query(value = "select count(ac.id) from AccumHashtagHistory as ac " +
            "where ac.studycafe.id = :studycafeId " +
            "group by ac.name " +
            "order by count(ac.id) desc ")
    List<AccumHashtagHistory> findTop5ByStudycafeId(@Param("studycafeId") Long studycafeId);
}
