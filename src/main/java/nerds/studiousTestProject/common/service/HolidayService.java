package nerds.studiousTestProject.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class HolidayService {
    private final String HOLIDAY_URL = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo";
    private final String SERVICE_KEY = "tydXwQmbYkWWLYepYjjXZNUJ9ZupgMXdPSqm7eCgPdLUP1It5h8GsXElWrjqXxj5vx2o6Ov4V9r0tExkFbKqoA%3D%3D";
    private final String _TYPE = "json";
    private final Integer THIS_MONTH = LocalDate.now().getMonth().getValue();
    private Integer NEXT_MONTH;
    private final Integer THIS_YEAR = LocalDate.now().getYear();
    private Integer NEXT_YEAR = THIS_YEAR;

    public List<LocalDate> getHolidayInfo() throws URISyntaxException {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(HOLIDAY_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        WebClient webClient = WebClient.builder().uriBuilderFactory(factory).baseUrl(HOLIDAY_URL).build();

        checkNewYear();

        Mono<Map> responseFirstMonth = getHoliday(webClient, THIS_YEAR, THIS_MONTH);
        Mono<Map> responseSecondMonth = getHoliday(webClient, NEXT_YEAR, NEXT_MONTH);

        Map<Integer, Object> holidayInfo = getAllHolidays(responseFirstMonth, responseSecondMonth);

        List<LocalDate> holidayInfoList = new ArrayList<>();

        getHolidayList(holidayInfo, holidayInfoList);
        return holidayInfoList;
    }

    private void checkNewYear() {
        NEXT_YEAR = THIS_MONTH == 12 ? LocalDate.now().plusYears(1).getYear() : THIS_YEAR;
        NEXT_MONTH = THIS_MONTH == 12 ? 1 : THIS_MONTH + 1;
    }

    private Map<Integer, Object> getAllHolidays(Mono<Map> responseFirstMonth, Mono<Map> responseSecondMonth) {
        Map<Integer, Object> holidayInfo =
                Mono.zip(responseFirstMonth, responseSecondMonth, (response1, response2) -> {
                            Map<Integer, Object> map = new HashMap<>();
                            map.put(THIS_MONTH, response1);
                            map.put(NEXT_MONTH, response2);
                            return map;
                        })
                        .block();
        return holidayInfo;
    }

    private Mono<Map> getHoliday(WebClient webClient, Integer THIS_YEAR, Integer THIS_MONTH) {
        Mono<Map> responseFirstMonth = webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("solYear", THIS_YEAR)
                        .queryParam("solMonth", String.format("%02d", THIS_MONTH))
                        .queryParam("ServiceKey", SERVICE_KEY)
                        .queryParam("_type", _TYPE)
                        .build())
                .retrieve()
                .bodyToMono(Map.class);
        return responseFirstMonth;
    }

    private void getHolidayList(Map<Integer, Object> holidayInfo, List<LocalDate> holidayInfoList) {
        for (int i = THIS_MONTH; i <= THIS_MONTH + 1; i++) {
            Map<String, Object> head = (Map<String, Object>) holidayInfo.get(i);
            Map<String, Object> response = (Map<String, Object>) head.get("response");
            Map<String, Object> body = (Map<String, Object>) response.get("body");
            HashMap<String, Object> items = (HashMap<String, Object>) body.get("items");
            ArrayList<HashMap<String, Object>> item = (ArrayList<HashMap<String, Object>>) items.get("item");
            makeHolidayList(holidayInfoList, item);
        }
    }

    private void makeHolidayList(List<LocalDate> holidayInfoList, ArrayList<HashMap<String, Object>> item) {
        for (HashMap<String, Object> itemMap : item) {
            Integer locdate = (Integer) itemMap.get("locdate");

            int year = locdate / 10000;
            int monthDay = locdate % 10000;
            int month = monthDay / 100;
            int day = monthDay % 100;
            LocalDate date = LocalDate.of(year, month, day);

            holidayInfoList.add(date);
        }
    }
}