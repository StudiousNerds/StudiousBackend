package nerds.studiousTestProject.photo.repository;

import nerds.studiousTestProject.photo.entity.SubPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubPhotoRepository extends JpaRepository<SubPhoto, Long> {
    List<SubPhoto> findAllByReviewId(Long reviewId);
    List<SubPhoto> findAllByStudycafeId(Long studycafeId);
    List<SubPhoto> findAllByRoomId(Long roomId);
    void deleteAllByReviewId(Long reviewId);
}
