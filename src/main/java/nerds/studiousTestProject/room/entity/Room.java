package nerds.studiousTestProject.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.user.entity.member.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "studycafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Studycafe studycafe;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String name;

    @Column(name = "stand_count")
    private Integer standCount; // 기존 인원수

    @Column(name = "min_count")
    private Integer minCount; // 최소 인원수

    @Column(name = "max_count")
    private Integer maxCount; // 최대 인원수

    private Integer price;

    private String type; // 가격 기준

    @Column(name = "min_using_time")
    private Integer minUsingTime; // 최소 이용 시간

    @Builder
    public Room(Long id, Studycafe studycafe, Member member, String name, Integer standCount, Integer minCount, Integer maxCount, Integer price, String type, Integer minUsingTime) {
        this.id = id;
        this.studycafe = studycafe;
        this.member = member;
        this.name = name;
        this.standCount = standCount;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.price = price;
        this.type = type;
        this.minUsingTime = minUsingTime;
    }
}
