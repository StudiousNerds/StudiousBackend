package nerds.studiousTestProject.room.repository;

import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import static nerds.studiousTestProject.fixture.RoomFixture.ROOM_FOUR_SIX;

@RepositoryTest
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

    @Test
    void id로_룸찾기(){
        Room save = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        Optional<Room> roomFind = roomRepository.findById(1L);
        Assertions.assertThat(save).isEqualTo(roomFind.get());
    }
}