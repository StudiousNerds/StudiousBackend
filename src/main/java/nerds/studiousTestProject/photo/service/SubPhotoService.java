package nerds.studiousTestProject.photo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class SubPhotoService {
    private final SubPhotoRepository subPhotoRepository;
    private final StudycafeRepository studycafeRepository;

    public String[] findReviewPhotos(Long id){
        List<SubPhoto> photoList = subPhotoRepository.findAllByReviewId(id);
        Integer arrSize = photoList.size();
        String reviewPhotos[] = new String[arrSize];

        for (int i = 0; i < arrSize; i++){
            reviewPhotos[i] = photoList.get(i).getUrl();
        }

        return reviewPhotos;
    }

    public String[] findCafePhotos(Long id){
        List<SubPhoto> photoList = subPhotoRepository.findAllByStudycafeId(id);
        Studycafe studycafe = studycafeRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        Integer arrSize = photoList.size();
        String cafePhotos[] = new String[arrSize];
        cafePhotos[0] = studycafe.getPhoto();

        for (int i = 1; i < arrSize; i++){
            cafePhotos[i] = photoList.get(i).getUrl();
        }

        return cafePhotos;
    }

    public String[] findRoomPhotos(Long id){
        List<SubPhoto> photoList = subPhotoRepository.findAllByRoomId(id);
        Integer arrSize = photoList.size();
        String roomPhotos[] = new String[arrSize];

        for (int i = 0; i < arrSize; i++){
            roomPhotos[i] = photoList.get(i).getUrl();
        }

        return roomPhotos;
    }

    public void savePhoto(SubPhoto photo) {
        subPhotoRepository.save(photo);
    }

    public void removePhoto(Long reviewId) {
        subPhotoRepository.deleteAllByReviewId(reviewId);
    }
}
