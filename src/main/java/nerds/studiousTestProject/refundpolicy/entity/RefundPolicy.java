package nerds.studiousTestProject.refundpolicy.entity;

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
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "remaining", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Remaining remaining;

    @Column(name = "rate",nullable = false)
    private Integer rate;

    @Column(name = "type",nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id", nullable = false)
    private Studycafe studycafe;

    @Builder
    public RefundPolicy(Long id, Remaining remaining, Integer rate, String type, Studycafe studycafe) {
        this.id = id;
        this.remaining = remaining;
        this.rate = rate;
        this.type = type;
        this.studycafe = studycafe;
    }

    public void setStudycafe(Studycafe studycafe) {
        if (studycafe != null) {
            this.studycafe = studycafe;
            this.type = RefundPolicyType.STUDYCAFE;
        }
    }
}
