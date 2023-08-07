package nerds.studiousTestProject.room.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String name;

    private Integer standardHeadCount; // 기존 인원수
    private Integer minHeadCount;
    private Integer maxHeadCount;

    private Integer price;
    private Integer minUsingTime;

    @Enumerated(value = EnumType.STRING)
    private PriceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ReservationRecord> reservationRecords = new ArrayList<>();

    // 다대일 양방향 연관관계
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Convenience> conveniences = new ArrayList<>();

    public void addReservationRecord(ReservationRecord reservationRecord) {
        reservationRecords.add(reservationRecord);
        reservationRecord.setRoom(this);
    }

    public void addConvenience(Convenience convenience) {
        conveniences.add(convenience);
        convenience.setRoom(this);
    }

    @Builder
    public Room(Long id, Studycafe studycafe, String name, Integer standardHeadCount, Integer minHeadCount, Integer maxHeadCount, Integer price, Integer minUsingTime, PriceType type) {
        this.id = id;
        this.studycafe = studycafe;
        this.name = name;
        this.standardHeadCount = standardHeadCount;
        this.minHeadCount = minHeadCount;
        this.maxHeadCount = maxHeadCount;
        this.price = price;
        this.minUsingTime = minUsingTime;
        this.type = type;
    }

    public void setStudycafe(Studycafe studycafe) {
        this.studycafe = studycafe;
    }
}