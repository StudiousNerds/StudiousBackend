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

import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_REVIEW;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class SubPhotoService {
    private final SubPhotoRepository subPhotoRepository;
    private final StudycafeRepository studycafeRepository;
    private final ReviewRepository reviewRepository;

    public String[] findReviewPhotos(Long reviewId){
        List<SubPhoto> photoList = subPhotoRepository.findAllByReviewId(reviewId);
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(NOT_FOUND_REVIEW));
        Integer arrSize = photoList.size();
        String reviewPhotos[] = new String[arrSize];
        reviewPhotos[0] = review.getPhoto();

        for (int i = 1; i < arrSize; i++){
            reviewPhotos[i] = photoList.get(i).getPath();
        }

        return reviewPhotos;
    }

    public String[] findCafePhotos(Long studycafeId){
        List<SubPhoto> photoList = subPhotoRepository.findAllByStudycafeId(studycafeId);
        Studycafe studycafe = studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        Integer arrSize = photoList.size();
        String cafePhotos[] = new String[arrSize];
        cafePhotos[0] = studycafe.getPhoto();

        for (int i = 1; i < arrSize; i++){
            cafePhotos[i] = photoList.get(i).getPath();
        }

        return cafePhotos;
    }

    public String[] findRoomPhotos(Long roomId){
        List<SubPhoto> photoList = subPhotoRepository.findAllByRoomId(roomId);
        Integer arrSize = photoList.size();
        String roomPhotos[] = new String[arrSize];

        for (int i = 0; i < arrSize; i++){
            roomPhotos[i] = photoList.get(i).getPath();
        }

        return roomPhotos;
    }

    public void savePhoto(SubPhoto photo) {
        subPhotoRepository.save(photo);
    }

    public void removeAllPhotos(Long reviewId) {
        subPhotoRepository.deleteAllByReviewId(reviewId);
    }
}
