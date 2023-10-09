package nerds.studiousTestProject.common.service;

import io.netty.handler.codec.http.HttpScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.IntervalServerException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.WEB_CLIENT_ERROR;

@RequiredArgsConstructor
@Service
@Slf4j
public class HolidayFinder {
    private final WebClient webClient;

    // WebClient 요청에 사용되는 상수들
    private static final String REQUEST_HOST = "apis.data.go.kr";
    private static final String REQUEST_PATH = "/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
    private static final String SERVICE_KEY_PREFIX = "serviceKey";
    private static final String SOL_YEAR_PREFIX = "solYear";
    private static final String SOL_MONTH_PREFIX = "solMonth";
    private static final String RESPONSE_TYPE_PREFIX = "_type";
    private static final String SERVICE_KEY_VALUE = "FgCjyyTMbN42kaatvYSk8LsS1%2Bu6BY%2BW%2BrZWqjr7TkgZD%2BNt7HGM5WqjLkG60PdNroVSEQTK9JcwunkeQ%2F%2F3kQ%3D%3D";
    private static final String RESPONSE_TYPE_VALUE = "json";


    // WebClient 응답 결과 검증에 사용되는 상수들
    private static final String SUCCESS_RESPONSE_CODE = "00";
    private static final String SUCCESS_RESPONSE_LOG_FORMAT = "공휴일 데이터 API를 성공적으로 가져왔습니다.";
    private static final String FAIL_RESPONSE_LOG_FORMAT = "공휴일 데이터 API를 가져오는 중 오류가 발생하였습니다. 에러 메시지 = {}";

    /**
     * 이번/다음 달의 공휴일 날짜를 계산하는 메소드
     * @return 이번/다음 달의 공휴일 날짜를 LocalDate 타입으로 담긴 리스트
     */
    public List<LocalDate> getTotalMonthHolidayDateInfos() throws Exception {
        int year = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        int nextMonth = getNextMonth(currentMonth);

        List<LocalDate> currentMonthHolidays = getOneMonthHolidayDateInfos(year, currentMonth);
        List<LocalDate> nextMonthHolidays = getOneMonthHolidayDateInfos(year, nextMonth);

        // 공휴일 데이터 API 응답에 오류가 생긴 경우
        if (currentMonthHolidays == null || nextMonthHolidays == null) {
            throw new IntervalServerException(WEB_CLIENT_ERROR);
        }

        List<LocalDate> totalHolidays = new ArrayList<>();
        totalHolidays.addAll(currentMonthHolidays);
        totalHolidays.addAll(nextMonthHolidays);

        return totalHolidays;
    }

    /**
     * year, month의 공휴일 정보를 반환하는 메소드
     * @param year 년도
     * @param month 월
     * @return 공휴일 날짜가 LocalDate 타입으로 담긴 리스트
     */
    private List<LocalDate> getOneMonthHolidayDateInfos(int year, int month) {
        Map<String, Object> response = getHolidayApiResponse(year, month);
        validateResultCode(response);

        List<String> parseResponse = parseResponse(response);
        return parseResponse.stream().map(this::stringToLocalDate).toList();
    }

    /**
     * @param year 공휴일 조회 연도
     * @param month 공휴일 조회 월
     * @return 해당 년도/월에 모든 공휴일 날짜
     */
    private Map<String, Object> getHolidayApiResponse(int year, int month) {
        Map<String, Object> resultMap = webClient
                .get()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme(HttpScheme.HTTPS.toString())
                                .host(REQUEST_HOST)
                                .path(REQUEST_PATH)
                                .queryParam(SERVICE_KEY_PREFIX, SERVICE_KEY_VALUE)
                                .queryParam(SOL_YEAR_PREFIX, year)
                                .queryParam(SOL_MONTH_PREFIX, parseMonthValue(month))
                                .queryParam(RESPONSE_TYPE_PREFIX, RESPONSE_TYPE_VALUE)
                                .build()
                                .toUriString()
                )
                .header(MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        return (Map<String, Object>) resultMap.get("response");
    }

    /**
     * 공휴일 데이터 API 응답을 검증하는 메소드
     * @param response 공휴일 데이터 API 응답
     * @return 검증 결과
     */
    private void validateResultCode(Map<String, Object> response) throws WebClientResponseException {
        Map<String, Object> header = (Map<String, Object>) response.get("header");
        log.info(header.toString());
        String resultCode = String.valueOf(header.get("resultCode"));
        if (!resultCode.equals(SUCCESS_RESPONSE_CODE)) {
            String errorMessage = String.valueOf(header.get("returnAuthMsg"));
            log.error(FAIL_RESPONSE_LOG_FORMAT, errorMessage);
            throw new WebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, null, null, null, null);
        }

        log.info(SUCCESS_RESPONSE_LOG_FORMAT);
    }

    /**
     * 공휴일 데이터 API 응답에서 공휴일 날짜만 가져오는 메소드
     * @param response 공휴일 데이터 API 응답
     * @return 공휴일 날짜가 YYYYMMDD 형태의 String 으로 담긴 리스트
     */
    private List<String> parseResponse(Map<String, Object> response) {
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> items = (Map<String, Object>) body.get("items");
        List<Map<String, Object>> item = (List<Map<String, Object>>) items.get("item");
        return item.stream().map(m -> String.valueOf(m.get("locdate"))).toList();
    }

    private String parseMonthValue(int month) {
        StringBuilder stringBuilder = new StringBuilder();

        if (month < 10) {
            stringBuilder.append(0);
        }

        return stringBuilder.append(month).toString();
    }

    private int getNextMonth(int month) {
        return month < 12 ? month + 1 : 1;
    }

    /**
     * 20230903 같은 8자리 문자열을 LocalDate 타입으로 바꿔주는 메소드
     * @param s YYYYMMDD 형식의 문자열
     * @return LocalDate
     */
    private LocalDate stringToLocalDate(String s) {
        return LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
    }
}
