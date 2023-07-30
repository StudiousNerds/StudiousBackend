package nerds.studiousTestProject.convenience.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
import nerds.studiousTestProject.hashtag.entity.Hashtag;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvenienceService {
    private final ConvenienceRepository convenienceRepository;

    public String[] getAllCafeConveniences(Long studycafeId){
        List<String> convenienceList = convenienceRepository.findByStudycafeId(studycafeId);
        Integer arrSize = convenienceList.size();
        String cafeConveniences[] = convenienceList.toArray(new String[arrSize]);

        return cafeConveniences;
    }

    public String[] getAllRoomConveniences(Long studycafeId, Long roomId){
        List<String> convenienceList = convenienceRepository.findByStudycafeIdAndRoomId(studycafeId, roomId);
        Integer arrSize = convenienceList.size();
        String roomConveniences[] = convenienceList.toArray(new String[arrSize]);

        return roomConveniences;
    }
}
