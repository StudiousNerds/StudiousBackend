package nerds.studiousTestProject.hashtag.entity;

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
import nerds.studiousTestProject.review.entity.Review;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HashtagRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private HashtagName name;

    public void setReview(Review review) {
        this.review = review;
    }

    @Builder
    public HashtagRecord(Long id ,Review review, HashtagName name) {
        this.id = id;
        this.review = review;
        this.name = name;
    }
}
