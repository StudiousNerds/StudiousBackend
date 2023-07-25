package nerds.studiousTestProject.convenience.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Convenience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Room room;

    @ManyToOne
    @JoinColumn(name = "studycafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Studycafe studycafe;

    @Enumerated(EnumType.STRING)
    private ConvenienceName name;

    private String price;
}
