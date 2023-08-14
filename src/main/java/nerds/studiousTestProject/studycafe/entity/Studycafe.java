package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.room.entity.Room;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Studycafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private String name;
    private String photo;

    @Embedded
    private Address address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<OperationInfo> operationInfos = new ArrayList<>();

    private Integer walkingTime;

    @Column(name = "nearest_station")
    private String nearestStation;

    @Column(name = "accum_reserve_count")
    private Integer accumReserveCount;

    private String introduction;
    private LocalDateTime createdAt;
    private Double totalGrade;

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<NotificationInfo> notificationInfos = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<HashtagRecord> hashtagRecords = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<Convenience> conveniences = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<RefundPolicy> refundPolicies = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<SubPhoto> subPhotos = new ArrayList<>();

    public void addRoom(Room room) {
        rooms.add(room);
        room.setStudycafe(this);
    }

    public void addNotice(Notice notice) {
        notices.add(notice);
        notice.setStudycafe(this);
    }

    public void addConvenience(Convenience convenience) {
        conveniences.add(convenience);
        convenience.setStudycafe(this);
    }

    public void addHashtagRecord(HashtagRecord hashtagRecord) {
        hashtagRecords.add(hashtagRecord);
        hashtagRecord.setStudycafe(this);
    }

    public void addOperationInfo(OperationInfo operationInfo) {
        operationInfos.add(operationInfo);
        operationInfo.setStudycafe(this);
    }

    public void addRefundPolicy(RefundPolicy refundPolicy) {
        refundPolicies.add(refundPolicy);
        refundPolicy.setStudycafe(this);
    }

    public void addSubPhoto(SubPhoto subPhoto) {
        subPhotos.add(subPhoto);
        subPhoto.setStudycafe(this);
    }

    public void addNotificationInfo(NotificationInfo notificationInfo) {
        notificationInfos.add(notificationInfo);
        notificationInfo.setStudycafe(this);
    }

    public void updateIntroduction(String introduction) {
        if (introduction != null) {
            this.introduction = introduction;
        }
    }

    public void updateOperationInfos(List<OperationInfo> operationInfos) {
        if (operationInfos != null) {
            this.operationInfos = operationInfos;
        }
    }

    public void updateConveniences(List<Convenience> conveniences) {
        if (conveniences != null) {
            this.conveniences = conveniences;
        }
    }

    public void updateNotices(List<Notice> notices) {
        if (notices != null) {
            this.notices = notices;
        }
    }

    public void updateRefundPolices(List<RefundPolicy> refundPolicies) {
        if (refundPolicies != null) {
            this.refundPolicies = refundPolicies;
        }
    }

    public void addTotalGrade(Double totalGrade) {
        this.totalGrade = totalGrade;
    }

    @Builder
    public Studycafe(Long id, Member member, String name, Address address, String photo, String phoneNumber, Integer duration, String nearestStation, Integer accumReserveCount, String introduction, LocalDateTime createdAt, Double totalGrade) {
        this.id = id;
        this.member = member;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.walkingTime = duration;
        this.nearestStation = nearestStation;
        this.accumReserveCount = accumReserveCount;
        this.introduction = introduction;
        this.createdAt = createdAt;
        this.totalGrade = totalGrade;
    }
}
