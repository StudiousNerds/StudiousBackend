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
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccumHashtagHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private HashtagName name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id", nullable = false)
    private Studycafe studycafe;

    @Builder
    public AccumHashtagHistory(HashtagName name, Studycafe studycafe) {
        this.count = 0;
        this.name = name;
        this.studycafe = studycafe;
    }

    public void setStudycafe(Studycafe studycafe) {
        if (studycafe != null) {
            this.studycafe = studycafe;
        }
    }

    public void addCount() {
        count++;
    }

    public void subtractCount() {
        count--;
    }
    
    public void updateCount(Integer count) {
        if (count != null) {
            this.count += count;
        }
    }
}
