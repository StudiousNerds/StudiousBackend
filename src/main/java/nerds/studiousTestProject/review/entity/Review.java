package nerds.studiousTestProject.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ReservationRecord reservationRecord;

    @OneToOne
    @JoinColumn(name = "grade_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Grade grade;

    @Column(name = "created_date")
    private LocalDate createdDate;

    private String detail;

    private String comment;

    @Builder
    public Review(Long id, ReservationRecord reservationRecord, Grade grade, LocalDate createdDate, String detail, String comment) {
        this.id = id;
        this.reservationRecord = reservationRecord;
        this.grade = grade;
        this.createdDate = createdDate;
        this.detail = detail;
        this.comment = comment;
    }
}
