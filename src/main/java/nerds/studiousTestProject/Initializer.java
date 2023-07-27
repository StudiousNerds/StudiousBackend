package nerds.studiousTestProject;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class Initializer {
    private final RoomRepository roomRepository;
    private final StudycafeRepository studycafeRepository;

    @PostConstruct
    public void init(){
        Studycafe nerdsStudycafe = Studycafe.builder()
                .id(1L)
                .name("NerdsStudycafe")
                .endTime(LocalTime.of(23, 0))
                .tel("0212341234")
                .startTime(LocalTime.of(9, 0))
                .build();
        studycafeRepository.save(nerdsStudycafe);
        Room room1 = Room.builder()
                .id(1L)
                .maxHeadCount(6)
                .minHeadCount(4)
                .minUsingTime(60)
                .price(6000)
                .type("시간당")
                .name("testroom1")
                .studycafe(nerdsStudycafe)
                .build();
        roomRepository.save(room1);
    }
}
