package nerds.studiousTestProject.photo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubPhotoService {
    private final SubPhotoRepository subPhotoRepository;

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
        Integer arrSize = photoList.size();
        String cafePhotos[] = new String[arrSize];

        for (int i = 0; i < arrSize; i++){
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
}
