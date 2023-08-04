package nerds.studiousTestProject.refundpolicy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Entity
@Getter
public class RefundPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private RefundDay refundDay;
    private Integer rate;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

}
