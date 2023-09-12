package nerds.studiousTestProject.convenience.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_record_id", nullable = false)
    private ReservationRecord reservationRecord;

    @Column(name = "convenience_name", nullable = false)
    private String convenienceName;

    @Column(name = "price", nullable = false)
    private Integer price;
}
