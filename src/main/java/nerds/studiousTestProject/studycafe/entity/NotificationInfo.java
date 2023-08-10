package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class NotificationInfo {
    @Id
    @GeneratedValue
    private Long id;

    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;  // endDate 가 지나면 자동으로 DB에서 삭제되도록 하는 기능을 구현해야함

    @Builder
    public NotificationInfo(String detail, LocalDate startDate, LocalDate endDate) {
        this.detail = detail;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Studycafe studycafe;

    public void setStudycafe(Studycafe studycafe) {
        this.studycafe = studycafe;
    }
}
