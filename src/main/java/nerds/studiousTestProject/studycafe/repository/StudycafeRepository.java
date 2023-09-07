package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudycafeRepository extends JpaRepository<Studycafe, Long>, StudycafeRepositoryCustom {
    boolean existsByIdAndMember(Long id, Member member);
    Optional<Studycafe> findById(Long id);
    List<Studycafe> findTop10ByOrderByTotalGradeDesc();
    List<Studycafe> findTop10ByOrderByCreatedDateDesc();
    Page<Studycafe> findByMemberOrderByCreatedDateAsc(Member member, Pageable pageable);
    Optional<Studycafe> findByIdAndMember(Long id, Member member);
    Optional<Studycafe> deleteByIdAndMember(Long id, Member member);
}