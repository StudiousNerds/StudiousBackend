package nerds.studiousTestProject.room.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room findById(Long roomId){
        return roomRepository.findRoomById(roomId);
    }
}
