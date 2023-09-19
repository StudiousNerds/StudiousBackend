package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.support.RepositoryTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nerds.studiousTestProject.support.EntitySaveProvider.권한_저장;
import static nerds.studiousTestProject.support.EntitySaveProvider.회원_저장;
import static nerds.studiousTestProject.support.fixture.MemberFixture.KAKAO_USER;
import static nerds.studiousTestProject.support.fixture.RoleFixture.ADMIN;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.FIRST_STUDYCAFE;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.FOURTH_STUDYCAFE;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.SECOND_STUDYCAFE;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.THIRD_STUDYCAFE;

@RepositoryTest
public class StudycafeRepositoryTest {
    @Autowired
    private StudycafeRepository studycafeRepository;

    @Test
    void findTop10ByOrderByTotalGradeDesc() {
        // given
        Member admin = 회원_저장(KAKAO_USER.생성());
        권한_저장(ADMIN.멤버_생성(admin));
        Studycafe studycafe1 = 스터디카페_저장(FIRST_STUDYCAFE.멤버_생성(admin));
        Studycafe studycafe2 = 스터디카페_저장(FOURTH_STUDYCAFE.멤버_생성(admin));
        Studycafe studycafe3 = 스터디카페_저장(NERDS.멤버_생성(admin));
        Studycafe studycafe4 = 스터디카페_저장(SECOND_STUDYCAFE.멤버_생성(admin));
        Studycafe studycafe5 = 스터디카페_저장(THIRD_STUDYCAFE.멤버_생성(admin));
        // when
        List<Studycafe> top10ByOrderByTotalGradeDesc = studycafeRepository.findTop10ByOrderByTotalGradeDesc();
        // then
        Assertions.assertThat(top10ByOrderByTotalGradeDesc).containsExactly(studycafe1, studycafe5, studycafe2, studycafe4, studycafe3);
    }

    private Studycafe 스터디카페_저장(Studycafe studycafe) {
        return studycafeRepository.save(studycafe);
    }
}
