package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudycafeRepository extends JpaRepository<Studycafe, Long> {
    Optional<Studycafe> findById(Long id);
}
