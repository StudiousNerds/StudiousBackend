package nerds.studiousTestProject.convenience.entity;

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
public class Convenience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studycafe_id", nullable = true)
    private Studycafe studycafe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = true)
    private Room room;

    @Column(name = "name", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConvenienceName name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "is_free", nullable = false)
    private Boolean isFree;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConvenienceType type;

    public boolean isFree(){
        return this.isFree;
    }

    public void setStudycafe(Studycafe studycafe) {
        if (studycafe != null) {
            this.studycafe = studycafe;
            this.type = ConvenienceType.STUDYCAFE;
        }
    }

    public void setRoom(Room room) {
        if (room != null) {
            this.room = room;
            this.type = ConvenienceType.ROOM;
        }
    }
}
