package nerds.studiousTestProject.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SubPhotoService subPhotoService;

    private List<Review> getReviewList(Long id) {
        List<Review> reviewList = reviewRepository.findAllById(id);
        return reviewList;
    }
}
