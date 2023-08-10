package nerds.studiousTestProject.reservation.repository;

import nerds.studiousTestProject.RepositoryTest;
import nerds.studiousTestProject.ReservationRecordFixture;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nerds.studiousTestProject.RoomFixture.*;

@RepositoryTest
class ReservationRecordRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReservationRecordRepository reservationRecordRepository;

    @Test
    void findAllByRoomId() {
        // given
        Room room1 = roomRepository.save(ROOM_FOUR_SIX.생성(1L));
        ReservationRecord save1 = reservationRecordRepository.save(ReservationRecordFixture.FIRST_RESERVATION.룸_생성(room1, 1L));
        ReservationRecord save2 = reservationRecordRepository.save(ReservationRecordFixture.SECOND_RESERVATION.룸_생성(room1, 2L));
        // when
        List<ReservationRecord> reservationRecordList = reservationRecordRepository.findAllByRoomId(1L);
        // then
        Assertions.assertThat(reservationRecordList).contains(save1, save2);
    }
}