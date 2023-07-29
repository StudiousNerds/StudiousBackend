package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import nerds.studiousTestProject.user.entity.member.Member;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Studycafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String name;

    private String photo;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    private Integer duration;

    @Column(name = "nearest_station")
    private String nearestStation;

    @Column(name = "accum_reserve_count")
    private Integer accumReserveCount;

    private String introduction;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "total_grade")
    private Double totalGrade;

    @Column(name = "notification_info")
    private String notificationInfo;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> notice = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    @Column(name = "refund_policy_info")
    private List<Integer> refundPolicyInfo = new ArrayList<>();

    @Builder
    public Studycafe(Long id, Member member, String name, String photo, String phoneNumber, LocalTime startTime, LocalTime endTime, Integer duration, String nearestStation, Integer accumReserveCount, String introduction, LocalDateTime createdDate, Double totalGrade, String notificationInfo, List<String> notice, List<Integer> refundPolicyInfo) {
        this.id = id;
        this.member = member;
        this.name = name;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.nearestStation = nearestStation;
        this.accumReserveCount = accumReserveCount;
        this.introduction = introduction;
        this.createdDate = createdDate;
        this.totalGrade = totalGrade;
        this.notificationInfo = notificationInfo;
        this.notice = notice;
        this.refundPolicyInfo = refundPolicyInfo;
    }
}
