package nerds.studiousTestProject.review.repository;

import nerds.studiousTestProject.review.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
