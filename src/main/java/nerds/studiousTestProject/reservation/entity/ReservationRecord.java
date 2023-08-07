package nerds.studiousTestProject.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.member.entity.member.Member;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReservationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Room room;

    private String name;
    private String phoneNumber;

    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
    private Integer duration;
    private Integer headCount;

    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    // 편의시설 사용 여부
    @Column(name = "complete_payment")
    private Boolean completePayment;

    private String request;

    private String orderId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public void completePay(Payment payment) {
        this.status = ReservationStatus.CONFIRMED;
        this.payment = payment;
    }

    public void canceled() {
        this.status = ReservationStatus.CANCELED;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
