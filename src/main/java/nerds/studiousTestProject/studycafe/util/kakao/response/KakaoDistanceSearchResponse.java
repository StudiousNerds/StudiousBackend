package nerds.studiousTestProject.studycafe.util.kakao.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoDistanceSearchResponse {
    String x;
    String y;
    String name;
}
