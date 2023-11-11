package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Announcement;

import java.time.LocalDate;

@Builder
@Data
public class ModifyAnnouncementResponse {
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;

    public static ModifyAnnouncementResponse from(Announcement announcement) {
        return ModifyAnnouncementResponse.builder()
                .detail(announcement.getDetail())
                .startDate(announcement.getStartDate())
                .endDate(announcement.getEndDate())
                .build();
    }
}
