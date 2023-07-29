package nerds.studiousTestProject.studycafe.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.ConvenienceList;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.hashtag.HashtagRecord;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
public class Studycafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String tel;
    private String address;
    private String photo;

    @Nullable
    private Integer duration;   // 역까지 걸리는 시간 (분)
    @Nullable
    private String nearestStation;  // 가장 가까운 역 이름
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer accumReserveCount;
    private Double totalGrade;
    private String introduction;
    private LocalDateTime createdAt;

    @Nullable
    private String notificationInfo;
    private String notice;

    @OneToMany(mappedBy = "studycafe") // 반대쪽(주인)에 자신이 매핑되있는 필드명을 적는다
    private List<Room> rooms;

    @OneToMany
    @JoinColumn(name = "cafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<HashtagRecord> hashtagRecords;

    @OneToMany
    @JoinColumn(name = "cafe_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<ConvenienceList> convenienceLists;
}
