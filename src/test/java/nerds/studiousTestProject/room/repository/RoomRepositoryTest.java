package nerds.studiousTestProject.room.repository;

import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private StudycafeRepository studycafeRepository;

    @Test
    void findAllByStudycafeId() {
        // given
        Studycafe studycafe = studycafeRepository.save(Studycafe.builder().id(1L).build());
        Room room = roomRepository.save(Room.builder().id(1L).studycafe(studycafe).build());
        Room room1 = roomRepository.save(Room.builder().id(2L).studycafe(studycafe).build());
        // when
        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafe.getId());
        // then
        Assertions.assertThat(roomList).contains(room, room1);
    }
}