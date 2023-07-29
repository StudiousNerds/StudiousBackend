package nerds.studiousTestProject.room.repository;

import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.room.entity.Room;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static nerds.studiousTestProject.RoomFixture.ROOM_FOUR_SIX;
import static org.junit.jupiter.api.Assertions.*;


@RepositoryTest
class RoomRepositoryTest {

    @Autowired
    RoomRepository roomRepository;

    @Test
    void id로_룸찾기(){
        Room save = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        Optional<Room> roomFind = roomRepository.findById(1L);
        Assertions.assertThat(save).isEqualTo(roomFind.get());
    }


}