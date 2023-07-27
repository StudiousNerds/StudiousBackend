package nerds.studiousTestProject.studycafe.repository;

import jakarta.persistence.EntityNotFoundException;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudycafeRepositoryTest {
    @Autowired
    private StudycafeRepository studycafeRepository;

    private Studycafe cafe1, cafe2, cafe3, cafe4, cafe5, cafe6, cafe7, cafe8, cafe9, cafe10, cafe11;

    @BeforeEach
    public void setUp(){
        this.cafe1 = studycafeRepository.save(Studycafe.builder().id(1L).name("스프링카페").totalGrade(4.3).createdDate(LocalDateTime.now()).build());
        this.cafe2 = studycafeRepository.save(Studycafe.builder().id(2L).name("스프카페").totalGrade(3.5).createdDate(LocalDateTime.now().minusDays(1)).build());
        this.cafe3 = studycafeRepository.save(Studycafe.builder().id(3L).name("스링카페").totalGrade(4.0).createdDate(LocalDateTime.now().minusDays(10)).build());
        this.cafe4 = studycafeRepository.save(Studycafe.builder().id(4L).name("스프후카페").totalGrade(3.7).createdDate(LocalDateTime.now().minusDays(5)).build());
        this.cafe5 = studycafeRepository.save( Studycafe.builder().id(5L).name("프링카페").totalGrade(4.5).createdDate(LocalDateTime.now().minusDays(2)).build());
        this.cafe6 = studycafeRepository.save(Studycafe.builder().id(6L).name("링카페").totalGrade(5.0).createdDate(LocalDateTime.now().minusDays(4)).build());
        this.cafe7 = studycafeRepository.save(Studycafe.builder().id(7L).name("프카페").totalGrade(1.5).createdDate(LocalDateTime.now().minusDays(11)).build());
        this.cafe8 = studycafeRepository.save(Studycafe.builder().id(8L).name("스투카페").totalGrade(1.3).createdDate(LocalDateTime.now().minusMonths(1)).build());
        this.cafe9 = studycafeRepository.save(Studycafe.builder().id(9L).name("프파카페").totalGrade(0.5).createdDate(LocalDateTime.now().minusDays(15)).build());
        this.cafe10 = studycafeRepository.save(Studycafe.builder().id(10L).name("링파카페").totalGrade(2.5).createdDate(LocalDateTime.now().minusDays(12)).build());
        this.cafe11 = studycafeRepository.save(Studycafe.builder().id(11L).name("토비카페").totalGrade(2.0).createdDate(LocalDateTime.now().minusMonths(6)).build());
    }

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

    @Test
    @DisplayName("스터디카페의 이름이 주어졌을 때 해당 스터디카페를 잘 찾아오는지 확인")
    void findByName(){
        // given
        Studycafe studycafe = Studycafe.builder()
                .id(1L)
                .name("스프링카페")
                .build();
        Studycafe savedStudycafe = studycafeRepository.save(studycafe);
        // when
        Studycafe findByNameStudycafe = studycafeRepository.findByName(savedStudycafe.getName()).orElseThrow(() -> new EntityNotFoundException("No Such Studycafe"));
        // then
        Assertions.assertThat(findByNameStudycafe.getName()).isEqualTo("스프링카페");
    }

    @Test
    @DisplayName("평점이 높은 순으로 10개를 제대로 가져오는지 확인")
    void findTop10ByOrderByTotalGrade(){
        // given

        // when
        List<Studycafe> top10ByOrderByTotalGrade = studycafeRepository.findTop10ByOrderByTotalGradeDesc();
        // then
        Assertions.assertThat(top10ByOrderByTotalGrade).containsExactly(cafe6, cafe5, cafe1, cafe3, cafe4, cafe2, cafe10, cafe11, cafe7, cafe8);
    }

    @Test
    @DisplayName("최근 등록 순으로 10개를 제대로 가져오는지 확인")
    void findTop10ByOrderByCreatedDateDesc(){
        // given

        // when
        List<Studycafe> top10ByOrderByCreatedDate = studycafeRepository.findTop10ByOrderByCreatedDateDesc();
        //then
        Assertions.assertThat(top10ByOrderByCreatedDate).containsExactly(cafe1, cafe2, cafe5, cafe6, cafe4, cafe3, cafe7, cafe10, cafe9, cafe8);
    }
}
