package nerds.studiousTestProject.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.user.entity.member.Member;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ReservationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Room room;

    @ManyToOne
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Payment payment;

    private String name; // 예약자 이름

    @Column(name = "phone_number")
    private String phoneNumber;

    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    private Integer headcount;

    @Column(name = "reservation_status")
    private ReservationStatus reservationStatus;

    private String request;

    @Column(name = "complete_payment")
    private Boolean completePayment;

    @Builder
    public ReservationRecord(Long id, Member member, Room room, Payment payment, String name, String phoneNumber, LocalDate date, LocalTime startTime, LocalTime endTime, Integer headcount, ReservationStatus reservationStatus, String request, Boolean completePayment) {
        this.id = id;
        this.member = member;
        this.room = room;
        this.payment = payment;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headcount = headcount;
        this.reservationStatus = reservationStatus;
        this.request = request;
        this.completePayment = completePayment;
    }
}
