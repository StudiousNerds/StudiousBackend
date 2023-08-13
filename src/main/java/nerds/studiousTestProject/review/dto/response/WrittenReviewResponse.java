package nerds.studiousTestProject.review.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class WrittenReviewResponse {
    private Long reservationId;
    private Long studycafeId;
    private String studycafeName;
    private String studycafePhoto;
    private String roomName;
    private LocalDate date;
    private LocalDate writeDate;
    private Integer cleanliness;
    private Integer deafening;
    private Integer fixtureStatus;
    private String reviewPhoto;
    private String detail;
}
