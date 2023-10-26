package nerds.studiousTestProject.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@EnableScheduling
@RequiredArgsConstructor
@Service
@Slf4j
public class HolidayProvider {
    private final HolidayFinder holidayFinder;
    private final RedisProvider redisProvider;

    private static final String CRON_SCHEDULER_ONE_MONTH_VALUE = "0 0 0 1 * *"; // 매달 1일 자정
    private static final String REDIS_HOLIDAY_KEY = "holiday";
    private static final String FAIL_LOG_MESSAGE = "공휴일 데이터 API 요청 과정에서 오류가 발생해 저장하지 못하였습니다. 예외 메시지 = {}";
    private static final String SUCCESS_LOG_MESSAGE = "공휴일 데이터를 성공적으로 저장하였습니다.";

    /**
     * 매달 1일마다 Redis에 이번/다음 달 공휴일 정보를 저장하는 메소드
     */
    @Scheduled(cron = CRON_SCHEDULER_ONE_MONTH_VALUE)
    private void insertHolidayData() {
        List<LocalDate> holidayInfo;
        try {
            holidayInfo = holidayFinder.getTotalMonthHolidayDateInfos();
        } catch (Exception e) {
            log.error(FAIL_LOG_MESSAGE, e.getMessage());
            return;
        }

        for (LocalDate localDate : holidayInfo) {
            redisProvider.addSetData(REDIS_HOLIDAY_KEY, localDate);
        }

        log.info(SUCCESS_LOG_MESSAGE);
    }

    /**
     * 이번/다음 달 공휴일 데이터를 반환하는 메소드
     * @return 공휴일 날짜가 담긴 리스트
     */
    public List<LocalDate> getHolidays() {
        return redisProvider.getSetData(REDIS_HOLIDAY_KEY).stream().map(e -> (LocalDate) e).toList();
    }
}
