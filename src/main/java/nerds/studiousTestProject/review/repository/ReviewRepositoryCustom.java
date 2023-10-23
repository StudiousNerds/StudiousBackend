package nerds.studiousTestProject.review.repository;

import nerds.studiousTestProject.review.dto.enquiry.request.AdminReviewType;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<Review> getPagedReviewsByStudycafeId(Long studycafeId, AdminReviewType reviewType, Pageable pageable);
}
