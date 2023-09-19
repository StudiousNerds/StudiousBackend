package nerds.studiousTestProject.room.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import static nerds.studiousTestProject.support.EntitySaveProvider.룸_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.스터디카페_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.회원_저장;
import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.RoleFixture.ADMIN;
import static nerds.studiousTestProject.support.fixture.RoomFixture.ROOM_FOUR_SIX;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;

@RepositoryTest
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private StudycafeRepository studycafeRepository;

    @Test
    void findAllByStudycafeId() {
        // given
        Member admin = 회원_저장(DEFAULT_USER.생성());
        ADMIN.멤버_생성(admin);

        Studycafe studycafe = 스터디카페_저장(NERDS.멤버_생성(admin));
        Room room1 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe));
        Room room2 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe));
        Room room3 = 룸_저장(ROOM_FOUR_SIX.스터디카페_생성(studycafe));
        // when
        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafe.getId());
        // then
        Assertions.assertThat(roomList).hasSize(3);
    }

    @Test
    void id로_룸찾기(){
        Room save = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        Optional<Room> roomFind = roomRepository.findById(1L);
        Assertions.assertThat(save).isEqualTo(roomFind.get());
    }
}