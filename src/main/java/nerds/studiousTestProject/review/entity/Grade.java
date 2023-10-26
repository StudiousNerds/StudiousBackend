package nerds.studiousTestProject.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cleanliness", nullable = false)
    private Integer cleanliness;

    @Column(name = "deafening", nullable = false)
    private Integer deafening;

    @Column(name = "fixtures_status", nullable = false)
    private Integer fixturesStatus;

    @Column(name = "total", nullable = false)
    private Double total;

    @Builder
    public Grade(Long id, Integer cleanliness, Integer deafening, Integer fixturesStatus, Double total) {
        this.id = id;
        this.cleanliness = cleanliness;
        this.deafening = deafening;
        this.fixturesStatus = fixturesStatus;
        this.total = total;
    }

    public void updateTotal(Double total) {
        if (total != null) {
            this.total = total;
        }
    }

    private void updateCleanliness(Integer cleanliness) {
        if (cleanliness != null) {
            this.cleanliness = cleanliness;
        }
    }

    private void updateDeafening(Integer deafening) {
        if (deafening != null) {
            this.deafening = deafening;
        }
    }

    private void updateFixtureStatus(Integer fixturesStatus) {
        if (fixturesStatus != null) {
            this.fixturesStatus = fixturesStatus;
        }
    }

    public void update(Integer cleanliness, Integer deafening, Integer fixturesStatus, Double total){
        updateCleanliness(cleanliness);
        updateDeafening(deafening);
        updateFixtureStatus(fixturesStatus);
        updateTotal(total);
    }
}
