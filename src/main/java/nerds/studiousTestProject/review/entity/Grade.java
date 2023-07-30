package nerds.studiousTestProject.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Grade {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "grade")
    private Review review;

    private Integer cleanliness;

    private Integer deafening;

    @Column(name = "fixtures_status")
    private Integer fixturesStatus;

    @Column(name = "is_recommended")
    private Boolean isRecommended;

    private Double total;
}
