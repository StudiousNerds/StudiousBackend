package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefundPolicyResponse {
    private Integer day;    // 이용까지 남은 날짜
    private Integer rate;   // 환불 비율
}
