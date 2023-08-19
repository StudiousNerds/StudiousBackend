package nerds.studiousTestProject.review.dto.written.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GradeInfo {
    private Integer cleanliness;
    private Integer deafening;
    private Integer fixtureStatus;

    public static GradeInfo of(ReservationRecord reservationRecord) {
        return GradeInfo.builder()
                .cleanliness(reservationRecord.getReview().getGrade().getCleanliness())
                .deafening(reservationRecord.getReview().getGrade().getDeafening())
                .fixtureStatus(reservationRecord.getReview().getGrade().getFixturesStatus())
                .build();
    }
}
