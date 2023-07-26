package nerds.studiousTestProject.studycafe.repository;

import jakarta.persistence.EntityNotFoundException;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudycafeRepositoryTest {
    @Autowired
    private StudycafeRepository studycafeRepository;

    @Test
    @DisplayName("스터디카페의 id를 주었을 때 해당 스터디카페를 잘 찾아오는지 확인")
    void findById(){
        // given
        Studycafe studycafe = Studycafe.builder()
                .id(1L)
                .build();
        Studycafe savedStudycafe = studycafeRepository.save(studycafe);
        // when
        Studycafe findByIdStudycafe = studycafeRepository.findById(savedStudycafe.getId())
                .orElseThrow(() -> new EntityNotFoundException("No Such Studycafe"));
        // then
        Assertions.assertThat(findByIdStudycafe.getId()).isEqualTo(1L);
    }
}
