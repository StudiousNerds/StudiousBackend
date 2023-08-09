package nerds.studiousTestProject.review.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
public class AvailableReviewResponse {
    	private Long studycafeId;
        private String studycafeName;
        private String studycafePhoto;
        private Long reservationId;
        private String roomName;
        private String paymentType;
        private Integer price;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer duration;
        private LocalDate validDate;
}
