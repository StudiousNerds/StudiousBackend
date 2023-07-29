package nerds.studiousTestProject.room.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.exception.RoomNotFoundException;
import nerds.studiousTestProject.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room findRoomById(Long roomId){
        return roomRepository.findById(roomId)
                .orElseThrow(RoomNotFoundException::new);
    }
}
