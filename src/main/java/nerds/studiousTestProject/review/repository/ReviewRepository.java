package nerds.studiousTestProject.review.repository;

import io.lettuce.core.dynamic.annotation.Param;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
//    @Query(value = "select r " +
//            "from Review as r " +
//            "where r.reservationRecord.id in :reservationId " +
//            "order by r.createdDate desc")
//    List<Review> findTop3ByReservationRecordId(@Param("reservationId") List<Long> reservationId);

    // 스터디카페의 평균 청결도, 방음도를 가져올 때 필요한 메소드인데, 그 때는 Pageable를 받지 않아서 따로 구분 했습니다!
//    List<Review> findAllByReservationRecordIdInOrderByCreatedDateDesc(List<Long> reservationId);

//    Page<Review> findAllByReservationRecordIdIn(List<Long> reservationId, Pageable pageable);
}