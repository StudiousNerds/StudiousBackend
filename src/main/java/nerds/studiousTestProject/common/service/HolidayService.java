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
    private static final String HOLIDAY_URL = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo";
    private static final String SERVICE_KEY = "tydXwQmbYkWWLYepYjjXZNUJ9ZupgMXdPSqm7eCgPdLUP1It5h8GsXElWrjqXxj5vx2o6Ov4V9r0tExkFbKqoA%3D%3D";
    private static final String _TYPE = "json";
    private static final Integer THIS_MONTH = LocalDate.now().getMonth().getValue();
    private static final Integer NEXT_MONTH = LocalDate.now().getMonth().plus(1).getValue();
    private static final Integer THIS_YEAR = LocalDate.now().getYear();
    private static Integer NEXT_YEAR;
    private static String SOLYEAR = "solYear";
    private static String SOLMONTH = "solMonth";
    private static String SERVICEKEY = "ServiceKey";
    private static String TYPE = "_type";

    public List<LocalDate> getHolidayInfo() throws URISyntaxException {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(HOLIDAY_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        WebClient webClient = WebClient.builder().uriBuilderFactory(factory).baseUrl(HOLIDAY_URL).build();

        NEXT_YEAR = THIS_MONTH < 12 ? THIS_MONTH + 1 : 1;

        Mono<Map> responseFirstMonth = getHoliday(webClient, THIS_YEAR, THIS_MONTH);
        Mono<Map> responseSecondMonth = getHoliday(webClient, NEXT_YEAR, NEXT_MONTH);
        Map<String, Object> holidayInfo = getAllHolidays(responseFirstMonth, responseSecondMonth);

        List<LocalDate> holidayInfoList = new ArrayList<>();
        getHolidayList(holidayInfo, holidayInfoList);

        return holidayInfoList;
    }

    private void getHolidayList(Map<String, Object> holidayInfo, List<LocalDate> holidayInfoList) {
        for (int i = THIS_MONTH; i <= THIS_MONTH + 1; i++) {
            Map<String, Object> head = (Map<String, Object>) holidayInfo.get(String.valueOf(i));
            Map<String, Object> response = (Map<String, Object>) head.get("response");
            Map<String, Object> body = (Map<String, Object>) response.get("body");
            HashMap<String, Object> items = (HashMap<String, Object>) body.get("items");
            ArrayList<HashMap<String, Object>> item = (ArrayList<HashMap<String, Object>>) items.get("item");
            makeHolidayList(holidayInfoList, item);
        }
    }


    private Mono<Map> getHoliday(WebClient webClient, Integer thisYear, Integer thisMonth) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam(SOLYEAR, thisYear)
                        .queryParam(SOLMONTH, String.format("%02d", thisMonth))
                        .queryParam(SERVICEKEY, SERVICE_KEY)
                        .queryParam(TYPE, _TYPE)
                        .build())
                .retrieve()
                .bodyToMono(Map.class);
    }

    private Map<String, Object> getAllHolidays(Mono<Map> responseFirstMonth, Mono<Map> responseSecondMonth) {
        return Mono.zip(responseFirstMonth, responseSecondMonth, (response1, response2) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put(String.valueOf(THIS_MONTH), response1);
                    map.put(String.valueOf(NEXT_MONTH), response2);
                    return map;
                })
                .block();
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
