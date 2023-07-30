package nerds.studiousTestProject.review.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select r " +
            "from Review r " +
            "where r.reservationRecord = " +
            "(select res from ReservationRecord res where res.room = " +
            "(select ro from Room ro where ro.studycafe = :cafeId)) " +
            "order by r.createdDate desc", nativeQuery = true)
    List<Review> findAllById(@Param("cafeId") Long cafeId);
}
