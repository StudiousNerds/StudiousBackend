package nerds.studiousTestProject.studycafe.dto.modify.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
public class OperationInfoEditRequest {
    @NotNull(message = "요일 입력은 필수입니다.")
    @Valid
    private Week week;    // 요일 (추후, 열거체로 리펙토링)

    @NotNull(message = "시작 시간 입력은 필수입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;    // 시작 시간

    @NotNull(message = "마감 시간 입력은 필수입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;  // 마감 시간

    @NotNull(message = "24시간 여부를 체크해주세요.")
    private Boolean isAllDay; // 24시간 여부

    @NotNull(message = "휴일 여부를 체크해주세요.")
    private Boolean closed; // 휴일 여부

    public OperationInfo toEntity() {
        return OperationInfo.builder()
                .week(week)
                .startTime(startTime)
                .endTime(endTime)
                .isAllDay(isAllDay)
                .closed(closed)
                .build();
    }
}
