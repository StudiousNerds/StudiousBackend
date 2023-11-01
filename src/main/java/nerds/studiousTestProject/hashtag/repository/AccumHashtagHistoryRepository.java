package nerds.studiousTestProject.hashtag.repository;

import nerds.studiousTestProject.hashtag.entity.AccumHashtagHistory;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccumHashtagHistoryRepository extends JpaRepository<AccumHashtagHistory, Long> {
    Boolean existsByHashtagName(HashtagName hashtagName);
    Optional<AccumHashtagHistory> findByHashtagName(HashtagName hashtagName);
    List<AccumHashtagHistory> findAllByStudycafe(Studycafe studycafe);
}
