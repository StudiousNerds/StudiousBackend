package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class FindReviewResponse {
    private Double grade;
    private String nickname;
    private String roomName;
    private Integer minHeadCount;
    private Integer maxHeadCount;
    private String detail;
    private LocalDate date;
    private String[] photos;
}
