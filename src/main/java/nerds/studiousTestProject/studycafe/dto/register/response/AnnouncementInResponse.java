package nerds.studiousTestProject.studycafe.dto.register.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Announcement;

import java.time.LocalDate;

@Builder
@Data
public class AnnouncementInResponse {
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;

    public static AnnouncementInResponse from(Announcement announcement) {
        return AnnouncementInResponse.builder()
                .detail(announcement.getDetail())
                .startDate(announcement.getStartDate())
                .endDate(announcement.getEndDate())
                .build();
    }
}
