//package nerds.studiousTestProject.convenience.repository;
//
//import nerds.studiousTestProject.convenience.entity.Convenience;
//import nerds.studiousTestProject.room.entity.Room;
//import nerds.studiousTestProject.room.repository.RoomRepository;
//import nerds.studiousTestProject.studycafe.entity.Studycafe;
//import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.List;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class ConvenienceRepositoryTest {
//
//    @Autowired
//    private ConvenienceRepository convenienceRepository;
//    @Autowired
//    private StudycafeRepository studycafeRepository;
//    @Autowired
//    private RoomRepository roomRepository;
//
//    private Studycafe studycafe;
//    private Room room;
//
//    @BeforeEach
//    public void setUp(){
//        this.studycafe = studycafeRepository.save(Studycafe.builder().id(1L).build());
//        this.room = roomRepository.save(Room.builder().id(1L).build());
//
//        Convenience convenience = Convenience.builder()
//                .studycafe(studycafe)
//                .room(room)
//                .name(ConvenienceName.담요)
//                .build();
//        convenienceRepository.save(convenience);
//
//        Convenience convenience1 = Convenience.builder()
//                .studycafe(studycafe)
//                .name(ConvenienceName.담요)
//                .build();
//        convenienceRepository.save(convenience1);
//
//        Convenience convenience2 = Convenience.builder()
//                .studycafe(studycafe)
//                .name(ConvenienceName.음료)
//                .build();
//        convenienceRepository.save(convenience2);
//
//        Convenience convenience3 = Convenience.builder()
//                .studycafe(studycafe)
//                .room(room)
//                .name(ConvenienceName.주차)
//                .build();
//        convenienceRepository.save(convenience3);
//    }
//
//    @Test
//    void findByStudycafeId() {
//        // given
//
//        // when
//        List<String> convenienceList = convenienceRepository.findByStudycafeId(studycafe.getId());
//        // then
//        Assertions.assertThat(convenienceList).contains("담요", "음료");
//    }
//
//    @Test
//    void findByStudycafeIdAndRoomId() {
//        // given
//
//        // when
//        List<String> convenienceList = convenienceRepository.findByStudycafeIdAndRoomId(studycafe.getId(), room.getId());
//        // then
//        Assertions.assertThat(convenienceList).contains("담요", "주차");
//    }
//}