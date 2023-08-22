package nerds.studiousTestProject.reservation.entity;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.entity.Payment;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.member.entity.member.Member;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation_record")
public class ReservationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "user_name",nullable = false)
    private String userName;

    @Column(name = "user_phone_number",nullable = false)
    private String userPhoneNumber;

    @Column(name = "date",nullable = false)
    private LocalDate date;

    @Column(name = "start_time",nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time",nullable = false)
    private LocalTime endTime;

    @Column(name = "duration",nullable = false)
    private Integer duration;

    @Column(name = "head_count",nullable = false)
    private Integer headCount;

    @Column(name = "status",nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "request")
    private String request;

    @Column(name = "order_id")
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = true)
    private Review review;

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

    public void addReview(Review review) {
        if (review != null) {
            this.review = review;
        }
    }

    public void setRoom(Room room) {
        if (room != null) {
            this.room = room;
        }
    }
}
