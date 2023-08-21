package nerds.studiousTestProject.review.dto.written.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewInfo {
    private LocalDate writeDate;
    private String reviewPhoto;
    private String detail;

    public static ReviewInfo of(ReservationRecord reservationRecord) {
        return ReviewInfo.builder()
                .writeDate(reservationRecord.getReview().getCreatedDate())
                .reviewPhoto(reservationRecord.getReview().getPhoto())
                .detail(reservationRecord.getReview().getDetail())
                .build();
    }
}
