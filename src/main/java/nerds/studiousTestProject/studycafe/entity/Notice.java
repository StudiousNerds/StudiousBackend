package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue
    private Long id;

    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

    @Builder
    public Notice(Long id, String detail, Studycafe studycafe) {
        this.id = id;
        this.detail = detail;
        this.studycafe = studycafe;
    }

    public void setStudycafe(Studycafe studycafe) {
        this.studycafe = studycafe;
    }
}
