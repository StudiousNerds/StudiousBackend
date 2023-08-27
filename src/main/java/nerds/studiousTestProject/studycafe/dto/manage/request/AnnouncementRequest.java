package nerds.studiousTestProject.studycafe.dto.manage.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Announcement;
import nerds.studiousTestProject.studycafe.validator.manage.RegisterAnnouncementCheck;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Data
@RegisterAnnouncementCheck
public class AnnouncementRequest {
    @NotNull(message = "공지사항은 필수입니다.")
    @Length(min = 10, max = 100, message = "공지사항은 10 ~ 100자 사이여야 합니다.")
    private String detail;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "시작 날짜 형식이 잘못되었습니다.")
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$", message = "시작 날짜 형식이 잘못되었습니다.")
    private LocalDate endDate;

    public Announcement toEntity() {
        return Announcement.builder()
                .detail(detail)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
