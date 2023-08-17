package nerds.studiousTestProject.review.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

    @OneToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "cleanliness", nullable = false)
    private Integer cleanliness;

    @Column(name = "deafening", nullable = false)
    private Integer deafening;

    @Column(name = "fixtures_status", nullable = false)
    private Integer fixturesStatus;

    @Column(name = "is_recommended", nullable = false)
    private Boolean isRecommended;

    @Column(name = "total", nullable = false)
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

    public void updateTotal(Double total) {
        this.total = total;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void updateGrade(Integer cleanliness, Integer deafening, Integer fixturesStatus, Boolean isRecommended, Double total){
        this.cleanliness = cleanliness;
        this.deafening = deafening;
        this.fixturesStatus = fixturesStatus;
        this.isRecommended = isRecommended;
        this.total = total;
    }
}
