package nerds.studiousTestProject.room.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_ROOM;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room findRoomById(Long roomId){
        return roomRepository.findById(roomId)
                .orElseThrow(()->new NotFoundException(NOT_FOUND_ROOM));
    }
}
