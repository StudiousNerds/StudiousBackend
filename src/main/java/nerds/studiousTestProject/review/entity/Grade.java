package nerds.studiousTestProject.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder
    public Grade(Long id, Review review, Integer cleanliness, Integer deafening, Integer fixturesStatus, Boolean isRecommended, Double total) {
        this.id = id;
        this.review = review;
        this.cleanliness = cleanliness;
        this.deafening = deafening;
        this.fixturesStatus = fixturesStatus;
        this.isRecommended = isRecommended;
        this.total = total;
    }
}
