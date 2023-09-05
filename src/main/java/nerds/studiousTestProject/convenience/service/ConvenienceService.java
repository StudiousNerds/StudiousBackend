package nerds.studiousTestProject.convenience.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ConvenienceService {
    private final ConvenienceRepository convenienceRepository;

    @Transactional
    public void deleteRoomConveniences(Long roomId) {
        convenienceRepository.deleteAllByRoomId(roomId);
    }

    @Transactional
    public void saveRoomConveniences(List<Convenience> convenienceList) {
        convenienceRepository.saveAll(convenienceList);
    }
}
