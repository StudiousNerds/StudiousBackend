package nerds.studiousTestProject.studycafe.dto.modify.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Announcement;

import java.time.LocalDate;

@Builder
@Data
public class AnnouncementResponse {
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;

    public static AnnouncementResponse from(Announcement announcement) {
        return AnnouncementResponse.builder()
                .detail(announcement.getDetail())
                .startDate(announcement.getStartDate())
                .endDate(announcement.getEndDate())
                .build();
    }
}
