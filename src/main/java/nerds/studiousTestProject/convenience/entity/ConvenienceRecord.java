package nerds.studiousTestProject.convenience.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
public class ConvenienceRecord {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_record_id")
    private ReservationRecord reservationRecord;

    @ManyToOne
    @JoinColumn(name = "convenience_id")
    private Convenience convenience;
}
