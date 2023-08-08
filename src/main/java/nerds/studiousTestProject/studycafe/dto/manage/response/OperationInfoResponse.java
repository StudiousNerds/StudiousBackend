package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Week;

import java.time.LocalTime;

@Builder
@Data
public class OperationInfoResponse {
    private Week week;    // 요일 (추후, 열거체로 리펙토링)
    private LocalTime startTime;    // 시작 시간
    private LocalTime endTime;  // 마감 시간
    private Boolean allDay; // 24시간 여부
    private Boolean closed; // 휴일 여부
}
