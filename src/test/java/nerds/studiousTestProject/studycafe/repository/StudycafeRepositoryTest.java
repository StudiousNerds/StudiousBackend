package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.support.RepositoryTest;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.*;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.*;

@RepositoryTest
public class StudycafeRepositoryTest {
    @Autowired
    private StudycafeRepository studycafeRepository;

    private Studycafe cafe1, cafe2, cafe3, cafe4, cafe5, cafe6, cafe7, cafe8, cafe9, cafe10, cafe11;

    @BeforeEach
    public void setUp(){
        this.cafe1 = studycafeRepository.save(FIRST_STUDYCAFE.생성(1L));
        this.cafe2 = studycafeRepository.save(SECOND_STUDYCAFE.생성(2L));
        this.cafe3 = studycafeRepository.save(THIRD_STUDYCAFE.생성(3L));
        this.cafe4 = studycafeRepository.save(FOURTH_STUDYCAFE.생성(4L));
        this.cafe5 = studycafeRepository.save(FIFTH_STUDYCAFE.생성(5L));
        this.cafe6 = studycafeRepository.save(SIXTH_STUDYCAFE.생성(6L));
        this.cafe7 = studycafeRepository.save(SEVENTH_STUDYCAFE.생성(7L));
        this.cafe8 = studycafeRepository.save(EIGHTH_STUDYCAFE.생성(8L));
        this.cafe9 = studycafeRepository.save(NINETH_STUDYCAFE.생성(9L));
        this.cafe10 = studycafeRepository.save(TENTH_STUDYCAFE.생성(10L));
        this.cafe11 = studycafeRepository.save(ELEVENTH_STUDYCAFE.생성(11L));
    }

    @Test
    @DisplayName("스터디카페의 id를 주었을 때 해당 스터디카페를 잘 찾아오는지 확인")
    void findById(){
        // when
        Studycafe findByIdStudycafe = studycafeRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        // then
        Assertions.assertThat(findByIdStudycafe.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("스터디카페의 이름이 주어졌을 때 해당 스터디카페를 잘 찾아오는지 확인")
    void findByName(){
        // when
        Studycafe findByNameStudycafe = studycafeRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        // then
        Assertions.assertThat(findByNameStudycafe.getName()).isEqualTo("스프링카페");
    }

    @Test
    @DisplayName("평점이 높은 순으로 10개를 제대로 가져오는지 확인")
    void findTop10ByOrderByTotalGrade(){
        // when
        List<Studycafe> top10ByOrderByTotalGrade = studycafeRepository.findTop10ByOrderByTotalGradeDesc();
        // then
        Assertions.assertThat(top10ByOrderByTotalGrade).contains(cafe6, cafe5, cafe1, cafe3, cafe4, cafe2, cafe10, cafe11, cafe7, cafe8);
    }

    @Test
    @DisplayName("최근 등록 순으로 10개를 제대로 가져오는지 확인")
    void findTop10ByOrderByCreatedAtDesc(){
        // when
        List<Studycafe> top10ByOrderByCreatedDate = studycafeRepository.findTop10ByOrderByCreatedAtDesc();
        //then
        Assertions.assertThat(top10ByOrderByCreatedDate).contains(cafe1, cafe2, cafe5, cafe10, cafe6, cafe4, cafe9, cafe3, cafe7, cafe8);
    }
}
