package nerds.studiousTestProject.room.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@NoArgsConstructor
@Entity
@Getter
public class Room {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int minHeadCount;
    private int maxHeadCount;

    private int price;
    private String type;

    private int minUsingTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

    @Builder
    public Room(Long id, String name, int minHeadCount, int maxHeadCount, int price, String type, int minUsingTime, Studycafe studycafe) {
        this.id = id;
        this.name = name;
        this.minHeadCount = minHeadCount;
        this.maxHeadCount = maxHeadCount;
        this.price = price;
        this.type = type;
        this.minUsingTime = minUsingTime;
        this.studycafe = studycafe;
    }
}
