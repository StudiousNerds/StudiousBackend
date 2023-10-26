package nerds.studiousTestProject.review.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    @Query(
            value = "select r from Review r " +
                    "join fetch ReservationRecord r1 on r1.review.id = r.id " +
                    "join fetch Room r2 on r2.id = r1.room.id and r2.studycafe.id = :studycafeId",
            countQuery = "select count(r) from Review r " +
                    "join ReservationRecord r1 on r1.review.id = r.id " +
                    "join Room r2 on r2.id = r1.room.id and r2.studycafe.id = :studycafeId"
    )
    Page<Review> findAllByStudycafeId(@Param("studycafeId") Long studycafeId, Pageable pageable);

    @Query(
            value = "select r from Review r " +
                    "join fetch ReservationRecord r1 on r1.review.id = r.id " +
                    "join fetch Room r2 on r2.id = r1.room.id and r2.id = :roomId",
            countQuery = "select count(r) from Review r " +
                    "join ReservationRecord r1 on r1.review.id = r.id " +
                    "join Room r2 on r2.id = r1.room.id and r2.id = :roomId"
    )
    Page<Review> findAllByRoomId(@Param("studycafeId") Long roomId, Pageable pageable);

    @Query(
            value = "select r from Review r " +
                    "join fetch ReservationRecord r1 on r1.review.id = r.id " +
                    "join fetch Room r2 on r2.id = r1.room.id and r2.studycafe.id = :studycafeId "
    )
    List<Review> findAllByStudycafeIdWithoutPage(@Param("studycafeId") Long studycafeId);
}