package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Embeddable // 임베디드 활용
@Getter
@NoArgsConstructor  // 기본 생성자는 임베디드를 쓰기 위해선 필수
public class OperationInfo {
    @Enumerated(EnumType.STRING)
    private Week week;

    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean allDay;
    private Boolean closed;

    @Builder
    public OperationInfo(Week week, LocalTime startTime, LocalTime endTime, Boolean allDay, Boolean closed) {
        this.week = week;
        this.startTime = startTime;
        this.endTime = endTime;
        this.allDay = allDay;
        this.closed = closed;
    }
}
