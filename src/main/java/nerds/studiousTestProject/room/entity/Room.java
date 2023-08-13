package nerds.studiousTestProject.room.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import nerds.studiousTestProject.photo.entity.SubPhoto;
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

    private String name;

    private Integer standardHeadCount; // 기존 인원수
    private Integer minHeadCount;
    private Integer maxHeadCount;

    private Integer price;
    private Integer minUsingTime;

    private String photo;

    @Enumerated(value = EnumType.STRING)
    private PriceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<SubPhoto> subPhotos = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ReservationRecord> reservationRecords = new ArrayList<>();

    // 다대일 양방향 연관관계
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Convenience> conveniences = new ArrayList<>();

    public void addSubPhoto(SubPhoto subPhoto) {
        subPhotos.add(subPhoto);
        subPhoto.setRoom(this);
    }

    public void addReservationRecord(ReservationRecord reservationRecord) {
        reservationRecords.add(reservationRecord);
        reservationRecord.setRoom(this);
    }

    public void addConvenience(Convenience convenience) {
        conveniences.add(convenience);
        convenience.setRoom(this);
    }

    @Builder
    public Room(Long id, Studycafe studycafe, String name, Integer standardHeadCount, Integer minHeadCount, Integer maxHeadCount, Integer price, Integer minUsingTime, String photo, PriceType type) {
        this.id = id;
        this.studycafe = studycafe;
        this.name = name;
        this.standardHeadCount = standardHeadCount;
        this.minHeadCount = minHeadCount;
        this.maxHeadCount = maxHeadCount;
        this.price = price;
        this.minUsingTime = minUsingTime;
        this.photo = photo;
        this.type = type;
    }

    public void setStudycafe(Studycafe studycafe) {
        if (studycafe != null) {
            this.studycafe = studycafe;
        }
    }
}