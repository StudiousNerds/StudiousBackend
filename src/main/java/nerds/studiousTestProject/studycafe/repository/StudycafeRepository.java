package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudycafeRepository extends JpaRepository<Studycafe, Long>, StudycafeRepositoryCustom {
    boolean existsByIdAndMember(Long id, Member member);
    Optional<Studycafe> findById(Long id);

    @Query(value = "select s from Studycafe s " +
            "join fetch Room r on r.studycafe.id = s.id " +
            "join fetch ReservationRecord rr on rr.room.id = r.id " +
            "join fetch Review rrr on rr.review.id = rrr.id " +
            "join fetch Grade g on rrr.grade.id = g.id " +
            "group by s.id " +
            "order by (cast(s.gradeSum + sum(g.total) as double)) / (s.gradeCount + count(g)) desc " +
            "limit 10",
    countQuery = "select count(s) from Studycafe s " +
            "join fetch Room r on r.studycafe.id = s.id " +
            "join fetch ReservationRecord rr on rr.room.id = r.id " +
            "join fetch Review rrr on rr.review.id = rrr.id " +
            "join fetch Grade g on rrr.grade.id = g.id " +
            "group by s.id " +
            "order by (cast(s.gradeSum + sum(g.total) as double)) / (s.gradeCount + count(g)) desc " +
            "limit 10")
    List<Studycafe> findTop10ByOrderByTotalGradeDesc();
    List<Studycafe> findTop10ByOrderByCreatedDateDesc();
    Page<Studycafe> findByMemberOrderByCreatedDateAsc(Member member, Pageable pageable);
    Optional<Studycafe> findByIdAndMember(Long id, Member member);
    Optional<Studycafe> deleteByIdAndMember(Long id, Member member);
}