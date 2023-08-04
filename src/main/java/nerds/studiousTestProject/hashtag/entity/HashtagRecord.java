package nerds.studiousTestProject.hashtag.entity;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

    private Integer count;

    @Enumerated(EnumType.STRING)
    private HashtagName name;

    /**
     * 연관관계 편의 메소드
     * 스터디카페(1)를 저장할 때 HashtagRecord(N)의 연관관계가 만들어지도록 하기 위한 메소드
     * @param studycafe
     */
    public void setStudycafe(Studycafe studycafe) {
        this.studycafe = studycafe;
    }

    @Builder
    public HashtagRecord(Long id, Studycafe studycafe, Integer count, HashtagName name) {
        this.id = id;
        this.studycafe = studycafe;
        this.count = count;
        this.name = name;
    }
}
