package nerds.studiousTestProject.room.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;
}
