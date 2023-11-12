package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
public class RegisterOperationInfoRequest {
    @NotNull(message = "요일 입력은 필수입니다.")
    @Valid
    private Week week;

    @NotNull(message = "시작 시간 입력은 필수입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    @NotNull(message = "마감 시간 입력은 필수입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;

    @NotNull(message = "24시간 여부를 체크해주세요.")
    private Boolean isAllDay;

    @NotNull(message = "휴일 여부를 체크해주세요.")
    private Boolean closed;

    public OperationInfo toOperationInfo() {
        return OperationInfo.builder()
                .week(week)
                .startTime(startTime)
                .endTime(endTime)
                .isAllDay(isAllDay)
                .closed(closed)
                .build();
    }
}
