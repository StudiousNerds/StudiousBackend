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
import nerds.studiousTestProject.hashtag.entity.AccumHashtagHistory;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.room.entity.Room;
import org.springframework.data.annotation.CreatedDate;

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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "photo", nullable = false)
    private String photo;

    @Embedded
    private Address address;

    @Column(name = "tel", nullable = false)
    private String tel;

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<OperationInfo> operationInfos = new ArrayList<>();

    @Embedded
    private NearestStationInfo nearestStationInfo;

    @Column(name = "accum_reserve_count", nullable = true)
    private Integer accumReserveCount;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    @Column(name = "created_date", updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "total_grade", nullable = true)
    private Double totalGrade;

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)
    private List<Announcement> announcements = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "studycafe", cascade = CascadeType.ALL)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<AccumHashtagHistory> accumHashtagHistories = new ArrayList<>();

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

    public void addAccumHashtagHistory(AccumHashtagHistory accumHashtagHistory) {
        accumHashtagHistories.add(accumHashtagHistory);
        accumHashtagHistory.setStudycafe(this);
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

    public void addAnnouncement(Announcement announcement) {
        announcements.add(announcement);
        announcement.setStudycafe(this);
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
    public Studycafe(Long id, Member member, String name, Address address, String photo, String tel, NearestStationInfo nearestStationInfo, Integer accumReserveCount, String introduction, LocalDateTime createdDate, Double totalGrade) {
        this.id = id;
        this.member = member;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.tel = tel;
        this.nearestStationInfo = nearestStationInfo;
        this.accumReserveCount = accumReserveCount;
        this.introduction = introduction;
        this.createdDate = createdDate;
        this.totalGrade = totalGrade;
    }
}
