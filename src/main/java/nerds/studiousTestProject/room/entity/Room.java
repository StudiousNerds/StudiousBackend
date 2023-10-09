package nerds.studiousTestProject.room.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "min_head_count", nullable = false)
    private Integer minHeadCount;

    @Column(name = "max_head_count", nullable = false)
    private Integer maxHeadCount;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "min_using_time", nullable = false)
    private Integer minUsingTime;

    @Column(name = "price_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PriceType priceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id", nullable = false)
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
    public Room(Long id, Studycafe studycafe, String name, Integer minHeadCount, Integer maxHeadCount, Integer price, Integer minUsingTime, PriceType type) {
        this.id = id;
        this.studycafe = studycafe;
        this.name = name;
        this.minHeadCount = minHeadCount;
        this.maxHeadCount = maxHeadCount;
        this.price = price;
        this.minUsingTime = minUsingTime;
        this.priceType = type;
    }

    public void setStudycafe(Studycafe studycafe) {
        if (studycafe != null) {
            this.studycafe = studycafe;
        }
    }

    private void updateRoomName(String roomName) {
        if (roomName != null) {
            this.name = roomName;
        }
    }

    private void updateMinHeadCount(Integer minHeadCount) {
        if (minHeadCount != null) {
            this.minHeadCount = minHeadCount;
        }
    }

    private void updateMaxHeadCount(Integer maxHeadCount) {
        if (maxHeadCount != null) {
            this.maxHeadCount = maxHeadCount;
        }
    }

    private void updatePrice(Integer price) {
        if (price != null) {
            this.price = price;
        }
    }

    private void updateType(PriceType type) {
        if (type != null) {
            this.priceType = type;
        }
    }

    private void updateMinUsingTime(Integer minUsingTime) {
        if (minUsingTime != null) {
            this.minUsingTime = minUsingTime;
        }
    }

    public void deleteConveniences() {
        conveniences.removeAll(conveniences);
    }

    public void update(Room room) {
        updateRoomName(room.getName());
        updateMinHeadCount(room.getMinHeadCount());
        updateMaxHeadCount(room.getMaxHeadCount());
        updatePrice(room.getPrice());
        updateType(room.getPriceType());
        updateMinUsingTime(room.getMinUsingTime());
    }
}