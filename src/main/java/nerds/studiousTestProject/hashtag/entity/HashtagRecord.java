package nerds.studiousTestProject.hashtag.entity;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HashtagRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "studycafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Studycafe studycafe;

    private Integer count;

    @Enumerated(EnumType.STRING)
    private HashtagName name;

    @Builder
    public HashtagRecord(Long id, Studycafe studycafe, Integer count, HashtagName name) {
        this.id = id;
        this.studycafe = studycafe;
        this.count = count;
        this.name = name;
    }
}
