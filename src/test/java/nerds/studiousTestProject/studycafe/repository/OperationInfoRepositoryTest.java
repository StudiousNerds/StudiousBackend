package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("시작 시간을 찾아오는 테스트")
    void findStartTime() {
        // given
        OperationInfo operation = OperationInfo.builder().startTime(LocalTime.of(8,0,0)).week(Week.MONDAY).build();
        operationInfoRepository.save(operation);
        // when
        LocalTime startTime = operationInfoRepository.findStartTime(Week.of(LocalDate.now()))
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_START_TIME));
        // then
        Assertions.assertThat(startTime.getHour()).isEqualTo(8);
    }

    @Test
    @DisplayName("끝 시간을 찾아오는 테스트")
    void findEndTime() {
        // given
        OperationInfo operation = OperationInfo.builder().endTime(LocalTime.of(21,0,0)).week(Week.MONDAY).build();
        operationInfoRepository.save(operation);
        // when
        LocalTime endTime = operationInfoRepository.findEndTime(Week.of(LocalDate.now()))
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_END_TIME));
        // then
        Assertions.assertThat(endTime.getHour()).isEqualTo(21);
    }
}