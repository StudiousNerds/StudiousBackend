package nerds.studiousTestProject.review.dto.written.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

@Builder
@Data
public class WrittenReviewInfo {
    private Long reservationId;
    private StudycafeInfo studycafeInfo;
    private GradeInfo gradeInfo;
    private ReviewInfo reviewInfo;

    public static WrittenReviewInfo of(ReservationRecord reservationRecord) {
        return WrittenReviewInfo.builder()
                .reservationId(reservationRecord.getId())
                .studycafeInfo(StudycafeInfo.of(reservationRecord))
                .gradeInfo(GradeInfo.of(reservationRecord))
                .reviewInfo(ReviewInfo.of(reservationRecord))
                .build();
    }
}
