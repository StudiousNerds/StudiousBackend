package nerds.studiousTestProject.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.user.entity.member.Member;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReservationRecord {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne
    @JoinColumn(name = "studycafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Studycafe studycafe;

    @ManyToOne
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Payment payment;

    private String name; // 예약자 이름

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "reservation_time")
    private LocalDateTime reservationTime;

    private Integer headcount;

    @Column(name = "reservation_status")
    private String reservationStatus;

    private String request;

    @Column(name = "complete_payment")
    private Boolean completePayment;
}
