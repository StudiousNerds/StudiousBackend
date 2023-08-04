package nerds.studiousTestProject.studycafe.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.room.entity.Room;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Studycafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String name;
    private String address;
    private String photo;   // 추후 Photo 엔티티를 만들어 수정 예정

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<OperationInfo> operationInfos = new ArrayList<>();

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
    private List<String> notice = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "refund_policy_info")
    private List<Integer> refundPolicyInfo = new ArrayList<>();

    public void addOperationInfo(OperationInfo operationInfo) {
        operationInfos.add(operationInfo);
        operationInfo.setStudycafe(this);
    }

    public void addRoom(Room room) {
        rooms.add(room);
        room.setStudycafe(this);
    }

    public void addConvenience(Convenience convenience) {
        conveniences.add(convenience);
        convenience.setStudycafe(this);
    }

    public void addHashtagRecords(HashtagRecord hashtagRecord) {
        hashtagRecords.add(hashtagRecord);
        hashtagRecord.setStudycafe(this);
    }

    // 리스트 필드의 경우 생성자 파라미터에 추가하지 않는다. (NPE 발생 가능성)
    // 이들은 추후, addXXX 메소드를 통해서 값을 추가한다.
    @Builder
    public Studycafe(Long id, Member member, String name, String address, String photo, String phoneNumber, Integer duration, String nearestStation, Integer accumReserveCount, String introduction, LocalDateTime createdAt, Double totalGrade, @Nullable String notificationInfo, List<String> notice, List<Integer> refundPolicyInfo) {
        this.id = id;
        this.member = member;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.duration = duration;
        this.nearestStation = nearestStation;
        this.accumReserveCount = accumReserveCount;
        this.introduction = introduction;
        this.createdAt = createdAt;
        this.totalGrade = totalGrade;
        this.notificationInfo = notificationInfo;
        this.notice = notice;
        this.refundPolicyInfo = refundPolicyInfo;
    }
}
