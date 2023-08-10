package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class NotificationInfoResponse {
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;
}
