package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OperationInfoRepositoryTest {

    @Autowired
    private OperationInfoRepository operationInfoRepository;

    @Test
    void findStartTime() {
        // given
        OperationInfo operation = OperationInfo.builder().startTime(LocalTime.of(8,0,0)).week(Week.SATURDAY).build();
        operationInfoRepository.save(operation);
        // when
        LocalTime startTime = operationInfoRepository.findStartTime(Week.of(LocalDate.now()));
        // then
        Assertions.assertThat(startTime.getHour()).isEqualTo(8);
    }

    @Test
    void findEndTime() {
        // given
        OperationInfo operation = OperationInfo.builder().endTime(LocalTime.of(21,0,0)).week(Week.SATURDAY).build();
        operationInfoRepository.save(operation);
        // when
        LocalTime endTime = operationInfoRepository.findEndTime(Week.of(LocalDate.now()));
        // then
        Assertions.assertThat(endTime.getHour()).isEqualTo(21);
    }
}