package nerds.studiousTestProject.studycafe.util.kakao.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KakaoDistanceSearchRequest {
    String category_group_code;
    String x;
    String y;
    Integer radius;
    Integer page;
    Integer size;
    String sort;
}
