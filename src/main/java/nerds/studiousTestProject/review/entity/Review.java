package nerds.studiousTestProject.review.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.apache.catalina.LifecycleState;
import org.bouncycastle.pqc.crypto.newhope.NHOtherInfoGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_record_id")
    private ReservationRecord reservationRecord;

    @OneToMany(mappedBy = "review",
            orphanRemoval = true,
            fetch = FetchType.LAZY)   // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<HashtagRecord> hashtagRecords = new ArrayList<>();

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private Grade grade;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDate createdDate;

    @Column(name = "detail", nullable = false)
    private String detail;

    private String comment;

    public void addHashtagRecord(HashtagRecord hashtagRecord) {
        hashtagRecords.add(hashtagRecord);
        hashtagRecord.setReview(this);
    }

    public void addGrade(Grade grade) {
        this.grade = grade;
        grade.setReview(this);
    }

    @Builder
    public Review(Long id, Grade grade, LocalDate createdDate, String detail, String comment) {
        this.id = id;
        this.grade = grade;
        this.createdDate = createdDate;
        this.detail = detail;
        this.comment = comment;
    }

    public void updateDetail(String detail) {
        this.detail = detail;
    }

    public void setReservationRecord(ReservationRecord reservationRecord) {
        this.reservationRecord = reservationRecord;
    }
}
