package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.annotation.Nullable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.room.entity.Room;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Studycafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String name;
    private String address;
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
    private LocalDateTime createdAt;
    private Double totalGrade;

    @Column(name = "notification_info")
    @Nullable
    private String notificationInfo;

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<HashtagRecord> hashtagRecords = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<Convenience> conveniences = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> notice = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe")
    private List<RefundPolicy> refundPolicyList = new ArrayList<>();
}
