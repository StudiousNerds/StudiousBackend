package nerds.studiousTestProject.reservationRecord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.user.entity.member.Member;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReservationRecord {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String phoneNumber;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private int duration;
    private int headCount;
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus;
    private String request;
    private boolean completePayment; //편의시설 사용여부

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
