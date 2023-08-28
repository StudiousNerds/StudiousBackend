package nerds.studiousTestProject.review.repository;

import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 스터디카페의 평균 청결도, 방음도를 가져올 때 필요한 메소드인데, 그 때는 Pageable를 받지 않아서 따로 구분 했습니다!
    List<Review> findAllByIdInOrderByCreatedDateDesc(List<Long> reviewIds);

    Page<Review> findAllByIdIn(List<Long> reviewIds, Pageable pageable);
}