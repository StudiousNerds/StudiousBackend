package nerds.studiousTestProject.studycafe.util;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.dto.register.CreateRequest;
import nerds.studiousTestProject.studycafe.dto.register.PlaceResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NearestStationInfoCalculator {
    private final WebClient webClient;

    /**
     * 스터디카페의 가장 가까운 역까지 도보 거리를 계산하여 반환하는 메소드 (TMAP API 사용)
     * 참고 : https://tmapapi.sktelecom.com/main.html#webservice/docs/tmapRoutePedestrianDoc
     * @param request 스터디 카페 생성 DTO
     * @return 가장 가까운 역까지 도보 거리 (주변 지하철역이 없으면 null 반환)
     */
    public PlaceResponse getPlaceResponse(String latitude, String longitude) {
        Place place;
        PlaceResponse.PlaceResponseBuilder builder = PlaceResponse.builder();
        try {
            place = getPlace(latitude, longitude);
        } catch (Exception e) {
            // 주변 역이 없는 경우 필드에 null값을 담아 리턴
            return builder.build();
        }

        log.info("place = {}", place);
        MultiValueMap<String, String> params = MultiValueMapConverter.convert(
                TMapDistanceCalcRequest.builder()
                        .startX(longitude)
                        .startY(latitude)
                        .endX(place.x)
                        .endY(place.y)
                        .startName("cafe")
                        .endName("station")
                        .build()
        );

        Map<String, Object> map = webClient
                .post()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme("https")
                                .host("apis.openapi.sk.com")
                                .path("/tmap/routes/pedestrian")
                                .query("version={VERSION}")
                                .encode()
                                .buildAndExpand("1")
                                .toUri()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header("appKey", "121xik5nus14FswbOy5EF1DRDhQ3n2m68AoYAslX")
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        List<Map<String, Object>> features = (List<Map<String, Object>>) map.get("features");

        return builder
                .duration(calcDuration(features))
                .nearestStation(place.name)
                .build();
    }

    /**
     * 스터디카페와 가장 가까운 지하철역 위도, 경도 쌍을 반환하는 메소드
     * 참고 : https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
     * @param request 스터디 카페 생성 DTO
     * @return 지하철 역 위도, 경도 좌표
     */
    private Place getPlace(String latitude, String longitude) {
        MultiValueMap<String, String> params = MultiValueMapConverter.convert(
                KakaoMapDistanceSearchRequest.builder()
                        .category_group_code("SW8")
                        .x(longitude)
                        .y(latitude)
                        .radius(10000)
                        .page(1)
                        .size(1)
                        .sort("distance")
                        .build()
        );

        Map<String, Object> map = webClient
                .get()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme("https")
                                .host("dapi.kakao.com")
                                .path("/v2/local/search/category.{FORMAT}")
                                .queryParams(params)
                                .encode()
                                .buildAndExpand("json")
                                .toUri()
                )
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK 840802008a58093510f5294d2e7c67c9")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        // 아래 변수들이 null인 경우는 주변 역이 없다는 것을 의미
        List<Object> documents = (List<Object>) map.get("documents");
        Map<String, Object> document = (Map<String, Object>) documents.get(0);

        return Place.builder()
                .x((String) document.get("x"))
                .y((String) document.get("y"))
                .name((String) document.get("place_name"))
                .build();
    }

    @Nullable
    private Integer calcDuration(List<Map<String, Object>> features) {
        int duration = 0;
        for (Map<String, Object> feature : features) {
            Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
            duration += (int) properties.getOrDefault("time", 0);
        }

        return duration != 0 ? duration / 60 : null;    // 분 단위로 바꾸기
    }

    @Getter
    @Setter
    @Builder
    static class KakaoMapDistanceSearchRequest {
        String category_group_code;
        String x;
        String y;
        Integer radius;
        Integer page;
        Integer size;
        String sort;
    }

    @Getter
    @Setter
    @ToString
    @Builder
    static class Place {
        String x;
        String y;
        String name;
    }

    @Getter
    @Setter
    @Builder
    static class TMapDistanceCalcRequest {
        String startX;
        String startY;
        String endX;
        String endY;
        String startName;
        String endName;
    }
}
