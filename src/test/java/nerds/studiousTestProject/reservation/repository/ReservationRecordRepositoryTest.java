//package nerds.studiousTestProject.reservation.repository;
//
//import nerds.studiousTestProject.reservation.entity.ReservationRecord;
//import nerds.studiousTestProject.reservation.entity.ReservationStatus;
//import nerds.studiousTestProject.room.entity.Room;
//import nerds.studiousTestProject.room.repository.RoomRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class ReservationRecordRepositoryTest {
//
//    @Autowired
//    private ReservationRecordRepository reservationRecordRepository;
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Test
//    void findAllReservedTime() {
//        // given
//        Room room = roomRepository.save(Room.builder().id(1L).build());
//        reservationRecordRepository.save(ReservationRecord.builder().room(room).date(LocalDate.now())
//                .startTime(LocalTime.of(13, 0)).endTime(LocalTime.of(15, 0)).reservationStatus(ReservationStatus.RESERVED).build());
//        reservationRecordRepository.save(ReservationRecord.builder().room(room).date(LocalDate.now())
//                .startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(17, 0)).reservationStatus(ReservationStatus.RESERVED).build());
//        // when
//        List<Object[]> allReservedTime = reservationRecordRepository.findAllReservedTime(LocalDate.now(), room.getId());
//        // then
//        Assertions.assertThat(allReservedTime).hasSize(2);
//        Assertions.assertThat(allReservedTime.get(0)[0]).isEqualTo(13);
//        Assertions.assertThat((LocalTime) allReservedTime.get(0)[1]).isEqualTo(15);
//        Assertions.assertThat((LocalTime) allReservedTime.get(1)[0]).isEqualTo(16);
//        Assertions.assertThat((LocalTime) allReservedTime.get(1)[1]).isEqualTo(17);
//    }
//}