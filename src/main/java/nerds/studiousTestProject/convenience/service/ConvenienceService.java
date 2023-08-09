package nerds.studiousTestProject.convenience.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ConvenienceService {
    private final ConvenienceRepository convenienceRepository;

    /*
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

     */
}
