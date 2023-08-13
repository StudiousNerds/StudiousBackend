package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.NotificationInfo;

import java.time.LocalDate;

@Builder
@Data
public class NotificationInfoResponse {
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;

    public static NotificationInfoResponse from(NotificationInfo notificationInfo) {
        return NotificationInfoResponse.builder()
                .detail(notificationInfo.getDetail())
                .startDate(notificationInfo.getStartDate())
                .endDate(notificationInfo.getEndDate())
                .build();
    }
}
