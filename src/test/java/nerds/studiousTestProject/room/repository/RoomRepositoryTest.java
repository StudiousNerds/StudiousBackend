package nerds.studiousTestProject.room.repository;

import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.fixture.RoomFixture;
import nerds.studiousTestProject.fixture.StudycafeFixture;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import static nerds.studiousTestProject.fixture.RoomFixture.ROOM_FOUR_SIX;
import static nerds.studiousTestProject.fixture.StudycafeFixture.*;

@RepositoryTest
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private StudycafeRepository studycafeRepository;

    @Test
    void findAllByStudycafeId() {
        // given
        Studycafe studycafe = studycafeRepository.save(FIRST_STUDYCAFE.생성(1L));
        Room room = roomRepository.save(ROOM_FOUR_SIX.스터디카페_생성(studycafe, 1L));
        Room room1 = roomRepository.save(ROOM_FOUR_SIX.스터디카페_생성(studycafe,2L));
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