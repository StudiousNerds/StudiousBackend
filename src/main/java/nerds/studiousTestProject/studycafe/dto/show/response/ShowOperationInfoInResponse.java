package nerds.studiousTestProject.studycafe.dto.show.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;

import java.time.LocalTime;

@Builder
@Data
@ToString
public class ShowOperationInfoInResponse {
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final boolean closed;
    private final boolean isAllDay;

    public static ShowOperationInfoInResponse from(OperationInfo operationInfo) {
        return ShowOperationInfoInResponse.builder()
                .isAllDay(operationInfo.getIsAllDay())
                .closed(operationInfo.getClosed())
                .startTime(operationInfo.getStartTime())
                .endTime(operationInfo.getEndTime())
                .build();
    }
}
