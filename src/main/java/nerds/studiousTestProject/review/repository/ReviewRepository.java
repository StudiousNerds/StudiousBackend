package nerds.studiousTestProject.review.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select r " +
            "from Review as r " +
            "where r.reservationRecord.id = :reservationId " +
            "order by r.createdDate desc")
    List<Review> findTop3ByReservationRecordId(@Param("reservationId") Long reservationId);

    @Query(value = "select r " +
            "from Review as r " +
            "where r.reservationRecord.id = :reservationId " +
            "order by r.createdDate desc")
    List<Review> findAllByReservationRecordId(@Param("reservationId") Long reservationId);

    @Query(value = "select r " +
            "from Review as r " +
            "where r.reservationRecord.id = :reservationId " +
            "order by r.grade.total desc")
    List<Review> findAllByReservationRecordIdOrderByGradeDesc(@Param("reservationId") Long reservationId);

    @Query(value = "select r " +
            "from Review as r " +
            "where r.reservationRecord.id = :reservationId " +
            "order by r.grade.total asc")
    List<Review> findAllByReservationRecordIdOrderByGradeAsc(@Param("reservationId") Long reservationId);
}
