package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudycafeRepository extends JpaRepository<Studycafe, Long> {
}
