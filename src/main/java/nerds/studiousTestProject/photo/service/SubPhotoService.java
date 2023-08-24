package nerds.studiousTestProject.photo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_REVEIW;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class SubPhotoService {
    private final SubPhotoRepository subPhotoRepository;
    private final ReviewRepository reviewRepository;

    public List<String> findReviewPhotos(Long reviewId){
        List<SubPhoto> photoList = subPhotoRepository.findAllByReviewId(reviewId);
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVEIW));
        List<String> reviewPhotos = new ArrayList<>();
        reviewPhotos.add(review.getPhoto());
        List<String> reviewSubPhoto = photoList.stream().map(SubPhoto::getPath).collect(Collectors.toList());
        reviewPhotos.addAll(reviewSubPhoto);

        return reviewPhotos;
    }

    @Transactional
    public void saveAllPhotos(List<SubPhoto> photos) {
        subPhotoRepository.saveAll(photos);
    }

    @Transactional
    public void removeAllPhotos(Long reviewId) {
        subPhotoRepository.deleteAllByReviewId(reviewId);
    }
}
