package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Announcement;

import java.time.LocalDate;

@Builder
@Data
public class NotificationInfoResponse {
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;

    public static NotificationInfoResponse from(Announcement announcement) {
        return NotificationInfoResponse.builder()
                .detail(announcement.getDetail())
                .startDate(announcement.getStartDate())
                .endDate(announcement.getEndDate())
                .build();
    }
}
