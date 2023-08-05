package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
public class OperationInfoRequest {
    @NotNull(message = "요일 입력은 필수입니다.")
    @Valid
    private Week week;    // 요일 (추후, 열거체로 리펙토링)

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;    // 시작 시간

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;  // 마감 시간

    @NotNull(message = "24시간 여부를 체크해주세요.")
    private Boolean allDay; // 24시간 여부

    @NotNull(message = "휴일 여부를 체크해주세요.")
    private Boolean closed; // 휴일 여부
}