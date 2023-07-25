package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudycafeRepository extends JpaRepository<Studycafe, Long> {
    Optional<Studycafe> findById(Long id);
    List<Studycafe> findTop10ByOrderByTotalGardeDesc();
    List<Studycafe> findTop10ByOrderByCreatedDateDesc();
    Optional<Studycafe> findByName(String cafeName);
}
