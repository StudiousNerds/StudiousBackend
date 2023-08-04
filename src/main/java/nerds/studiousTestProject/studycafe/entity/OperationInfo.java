package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class OperationInfo {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Week week;

    @ManyToOne(fetch = FetchType.LAZY)
    private Studycafe studycafe;

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

    public void setStudycafe(Studycafe studycafe) {
        this.studycafe = studycafe;
    }
}
