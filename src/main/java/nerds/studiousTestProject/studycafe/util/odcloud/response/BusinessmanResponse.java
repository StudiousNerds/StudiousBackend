package nerds.studiousTestProject.studycafe.util.odcloud.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class BusinessmanResponse {
    private String status_code;     // 상태 코드
    private Integer match_cnt;      // 조회 결과 개수
    private Integer request_cnt;    // 조회 요청 개수
    private List<BusinessInfoData> data;        // 결과
}
