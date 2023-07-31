//package nerds.studiousTestProject.hashtag.repository;
//
//import jakarta.persistence.EntityNotFoundException;
//import nerds.studiousTestProject.hashtag.entity.Hashtag;
//import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
//import nerds.studiousTestProject.studycafe.entity.Studycafe;
//import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class HashtagRepositoryTest {
//
//    @Autowired
//    private HashtagRepository hashtagRepository;
//    @Autowired
//    private StudycafeRepository studycafeRepository;
//
//    @Test
//    void findByStudycafeId() {
//        // given
//        Studycafe studycafe = studycafeRepository.save(Studycafe.builder().id(1L).build());
//        List<Hashtag> hashtagList = new ArrayList<>();
//        hashtagList.add(Hashtag.갓성비);
//        hashtagList.add(Hashtag.면접용);
//        hashtagRepository.save(HashtagRecord.builder().id(1L).studycafe(studycafe).hashtags(hashtagList).build());
//        // when
//        HashtagRecord hashtagRecord = hashtagRepository.findByStudycafeId(studycafe.getId()).orElseThrow(() -> new EntityNotFoundException("No such Studycafe"));
//        List<Hashtag> hashtags = hashtagRecord.getHashtags();
//        // then
//        Assertions.assertThat(hashtags).contains(Hashtag.갓성비, Hashtag.면접용);
//    }
//}