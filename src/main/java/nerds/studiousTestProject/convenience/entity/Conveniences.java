package nerds.studiousTestProject.convenience.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
public class Conveniences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    private ConvenienceName name;
    private Integer price;
    private Boolean isFree;

    public boolean isFree(){
        return this.isFree;
    }

    public void setStudycafe(Studycafe studycafe) {
        this.studycafe = studycafe;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
