package nerds.studiousTestProject.hashtag.repository;

import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<HashtagRecord, Long> {
}