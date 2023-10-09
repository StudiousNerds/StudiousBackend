package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "week", nullable = false)
    @Enumerated(EnumType.STRING)
    private Week week;

    @JoinColumn(name = "studycafe_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Studycafe studycafe;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_all_day", nullable = false)
    private Boolean isAllDay;

    @Column(name = "closed", nullable = false)
    private Boolean closed;

    @Builder
    public OperationInfo(Week week, LocalTime startTime, LocalTime endTime, Boolean isAllDay, Boolean closed) {
        this.week = week;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAllDay = isAllDay;
        this.closed = closed;
    }

    public void setStudycafe(Studycafe studycafe) {
        if (studycafe != null) {
            this.studycafe = studycafe;
        }
    }
}
